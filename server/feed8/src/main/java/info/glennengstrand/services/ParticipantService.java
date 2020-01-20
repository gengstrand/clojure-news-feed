package info.glennengstrand.services;

import java.util.Optional;
import java.util.UUID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datastax.driver.core.utils.UUIDs;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;

import info.glennengstrand.api.Participant;
import info.glennengstrand.resources.NotFoundException;
import info.glennengstrand.resources.ParticipantApi;
import io.swagger.configuration.RedisConfiguration.ParticipantRedisTemplate;
import info.glennengstrand.dao.mysql.ParticipantRepository;

import info.glennengstrand.api.Outbound;
import info.glennengstrand.dao.cassandra.OutboundRepository;
import info.glennengstrand.dao.cassandra.InboundRepository;
import info.glennengstrand.util.Link;

import info.glennengstrand.api.Friend;
import info.glennengstrand.api.Inbound;
import io.swagger.configuration.RedisConfiguration.FriendRedisTemplate;
import io.swagger.configuration.RedisConfiguration.Friends;
import info.glennengstrand.dao.mysql.FriendRepository;

@Service
public class ParticipantService implements ParticipantApi {

	@Autowired
	private ParticipantRepository participantRepository;
	
	@Autowired
	private ParticipantRedisTemplate participantTemplate;
    
	@Autowired
	private FriendRepository friendRepository;
	
	@Autowired
	private FriendRedisTemplate friendTemplate;
	
    	@Autowired
	private OutboundRepository outboundRepository;

	@Autowired
	private InboundRepository inboundRepository;

    @Autowired
    private RestHighLevelClient esClient;

    private static Logger LOGGER = LoggerFactory.getLogger(ParticipantService.class.getCanonicalName());

    private void indexStory(String sender, String story) {
	    Map<String, String> doc = new HashMap<>();
	    doc.put(OutboundService.DOCUMENT_RESULT_FIELD, sender);
	    doc.put(OutboundService.DOCUMENT_SEARCH_FIELD, story);
	    IndexRequest req = new IndexRequest(OutboundService.DOCUMENT_INDEX, OutboundService.DOCUMENT_TYPE, UUID.randomUUID().toString()).source(doc);
	    try {
    		esClient.indexAsync(req, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {

    			@Override
    			public void onResponse(IndexResponse response) {
    				LOGGER.debug(response == null ? "no response" : response.toString());
    			}

    			@Override
    			public void onFailure(Exception e) {
    				LOGGER.warn("cannot index elasticsearch document: ", e);
    			}
    			
    		});
	    } catch (Exception e) {
    		LOGGER.warn("Cannot call elasticsearch: ", e);
	    }
	}
    
	@Override
	public Participant addParticipant(Participant body) {
		info.glennengstrand.dao.mysql.Participant p = new info.glennengstrand.dao.mysql.Participant();
		p.setMoniker(body.getName());
		info.glennengstrand.dao.mysql.Participant retVal = participantRepository.save(p);
		return body.link(Link.toLink(retVal.getId())).id(retVal.getId());
	}

	@Override
	public List<Inbound> getInbound(Long id) {
		return inboundRepository.findByNewsFeedItemKey_ParticipantId(id.intValue()).map(i -> {
			return new Inbound()
					.from(Link.toLink(i.getFromParticipantId()))
					.to(Link.toLink(id))
					.occurred(convert(UUIDs.unixTimestamp(i.getOccured())))
					.subject(i.getSubject())
					.story(i.getStory());
		}).collect(Collectors.toList());
	}

	public Inbound addInbound(Long id, Inbound body) {
		info.glennengstrand.dao.cassandra.Inbound i = new info.glennengstrand.dao.cassandra.Inbound();
		i.setParticipantId(id);
		i.setFromParticipantId(Link.extractId(body.getFrom()));
		i.setSubject(body.getSubject());
		i.setStory(body.getStory());
		inboundRepository.save(i);
		return body;
	}
    
	@Override
	public Participant getParticipant(Long id) {
		String key = "Participant::".concat(id.toString());
		if (participantTemplate.hasKey(key)) {
			return participantTemplate.boundValueOps(key).get();
		} else {
			Optional<info.glennengstrand.dao.mysql.Participant> r = participantRepository.findById(id);
			if (r.isPresent()) {
				info.glennengstrand.dao.mysql.Participant p = r.get();
				Participant retVal = new Participant().id(p.getId()).name(p.getMoniker()).link(Link.toLink(p.getId()));
				participantTemplate.boundValueOps(key).set(retVal);
				return retVal;
			} else {
				throw new NotFoundException(404, String.format("participant {} not found", id));
			}
		}
	}
    
	@Override
	public Friend addFriend(Long id, Friend body) {
		info.glennengstrand.dao.mysql.Friend p = new info.glennengstrand.dao.mysql.Friend();
		p.setFromParticipantId(id);
		p.setToParticipantId(Link.extractId(body.getTo()));
		info.glennengstrand.dao.mysql.Friend retVal = friendRepository.save(p);
		String fk = "Friend::".concat(body.getFrom().toString());
		friendTemplate.delete(fk);
		String tk = "Friend::".concat(body.getTo().toString());
		friendTemplate.delete(tk);
		return body.id(retVal.getId());
	}
    
    	@Override
	public List<Friend> getFriend(Long id) {
		String key = "Friend::".concat(id.toString());
		if (friendTemplate.hasKey(key)) {
			return friendTemplate.boundValueOps(key).get();
		} else {
			List<info.glennengstrand.dao.mysql.Friend> r1 = friendRepository.findByFromParticipantId(id);
			List<info.glennengstrand.dao.mysql.Friend> r2 = friendRepository.findByToParticipantId(id);
			if (r1.isEmpty() && r2.isEmpty()) {
				throw new NotFoundException(404, String.format("no friends for {}", id));
			} else {
				Friends retVal1 = r1.stream().map(dbf -> {
					return new Friend().id(dbf.getId()).from(Link.toLink(dbf.getFromParticipantId())).to(Link.toLink(dbf.getToParticipantId()));
				}).collect(Collectors.toCollection(() -> { return new Friends(); }));
				Friends retVal2 = r2.stream().map(dbf -> {
					return new Friend().id(dbf.getId()).from(Link.toLink(dbf.getToParticipantId())).to(Link.toLink(dbf.getFromParticipantId()));
				}).collect(Collectors.toCollection(() -> { return new Friends(); }));
				Friends retVal = Stream.concat(retVal1.stream(), retVal2.stream()).distinct().collect(Collectors.toCollection(() -> { return new Friends(); }));
				friendTemplate.boundValueOps(key).set(retVal);
				return retVal;
			}
		}
	}
    
	@Override
	public Outbound addOutbound(Long id, Outbound body) {
		info.glennengstrand.dao.cassandra.Outbound o = new info.glennengstrand.dao.cassandra.Outbound();
		o.setParticipantId(id);
		o.setSubject(body.getSubject());
		o.setStory(body.getStory());
		o = outboundRepository.save(o);
		getFriend(id).stream().forEach(f -> {
			info.glennengstrand.api.Inbound i = new info.glennengstrand.api.Inbound()
					.from(body.getFrom())
					.to(f.getTo())
					.subject(body.getSubject())
					.story(body.getStory());
			addInbound(Link.extractId(f.getTo()), i);
		});
		indexStory(Link.extractId(body.getFrom()).toString(), body.getStory());
		return body.occurred(convert(UUIDs.unixTimestamp(o.getOccured())));
	}

	@Override
	public List<Outbound> getOutbound(Long id) {
		return outboundRepository.findByNewsFeedItemKey_ParticipantId(id.intValue()).map(i -> {
			return new Outbound()
					.from(Link.toLink(i.getParticipantId()))
					.occurred(convert(UUIDs.unixTimestamp(i.getOccured())))
					.subject(i.getSubject())
					.story(i.getStory());
		}).collect(Collectors.toList());
	}

}
