package info.glennengstrand.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.utils.UUIDs;

import info.glennengstrand.api.Outbound;
import info.glennengstrand.dao.cassandra.OutboundRepository;
import info.glennengstrand.dao.elasticsearch.OutboundSearchRepository;
import info.glennengstrand.resources.FriendsApi;
import info.glennengstrand.resources.InboundApi;
import info.glennengstrand.resources.OutboundApi;

@Service
public class OutboundService implements OutboundApi {

	@Autowired
	private OutboundRepository repository;

    @Autowired
    private FriendsApi friendService;

    @Autowired
    private InboundApi inboundService;
    
    @Autowired
    private OutboundSearchRepository searchService;

	@Override
	public Outbound addOutbound(Outbound body) {
		info.glennengstrand.dao.cassandra.Outbound o = new info.glennengstrand.dao.cassandra.Outbound();
		o.setParticipantId(body.getFrom());
		o.setSubject(body.getSubject());
		o.setStory(body.getStory());
		o = repository.save(o);
		friendService.getFriend(body.getFrom()).stream().forEach(f -> {
			info.glennengstrand.api.Inbound i = new info.glennengstrand.api.Inbound()
					.from(body.getFrom())
					.to(f.getTo())
					.subject(body.getSubject())
					.story(body.getStory());
			inboundService.addInbound(i);
		});
		info.glennengstrand.dao.elasticsearch.Outbound so = new info.glennengstrand.dao.elasticsearch.Outbound();
		so.setSender(body.getFrom().intValue());
		so.setStory(body.getStory());
		searchService.save(so);
		return body.occurred(convert(UUIDs.unixTimestamp(o.getOccured())));
	}

	@Override
	public List<Outbound> getOutbound(Integer id) {
		return repository.findByNewsFeedItemKey_ParticipantId(id).map(i -> {
			return new Outbound()
					.from(i.getParticipantId())
					.occurred(convert(UUIDs.unixTimestamp(i.getOccured())))
					.subject(i.getSubject())
					.story(i.getStory());
		}).collect(Collectors.toList());
	}

	@Override
	public List<Integer> searchOutbound(String keywords) {
		return searchService.findByStory(keywords).map(so -> { return so.getSender(); }).collect(Collectors.toList());
	}

}
