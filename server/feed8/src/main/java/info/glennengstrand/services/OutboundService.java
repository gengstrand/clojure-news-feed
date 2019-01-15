package info.glennengstrand.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;

import info.glennengstrand.api.Outbound;
import info.glennengstrand.dao.cassandra.OutboundRepository;
import info.glennengstrand.resources.FriendsApi;
import info.glennengstrand.resources.InboundApi;
import info.glennengstrand.resources.OutboundApi;

@Service
public class OutboundService implements OutboundApi {

	private static Logger LOGGER = LoggerFactory.getLogger(OutboundService.class.getCanonicalName());
	private static final String DOCUMENT_INDEX = "feed";
	private static final String DOCUMENT_TYPE = "stories";
	private static final String DOCUMENT_SEARCH_FIELD = "story";
	private static final String DOCUMENT_RESULT_FIELD = "sender";

	@Autowired
	private OutboundRepository repository;

    @Autowired
    private FriendsApi friendService;

    @Autowired
    private InboundApi inboundService;
    
    @Autowired
    private RestHighLevelClient esClient;
    
    private void indexStory(String sender, String story) {
    	Map<String, String> doc = new HashMap<>();
    	doc.put(DOCUMENT_RESULT_FIELD, sender);
    	doc.put(DOCUMENT_SEARCH_FIELD, story);
    	IndexRequest req = new IndexRequest(DOCUMENT_INDEX, DOCUMENT_TYPE, UUID.randomUUID().toString()).source(doc);
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
    
    private List<Integer> searchStories(String keywords) {
    	SearchRequest req = new SearchRequest(DOCUMENT_INDEX).types(DOCUMENT_TYPE);
    	SearchSourceBuilder builder = new SearchSourceBuilder();
    	builder.query(QueryBuilders.termQuery(DOCUMENT_SEARCH_FIELD, keywords));
    	req.source(builder);
		try {
			SearchResponse resp = esClient.search(req);
	    	if (resp.status() == RestStatus.OK) {
	    		SearchHit[] hits = resp.getHits().getHits();
	    		List<Integer> retVal = new ArrayList<>();
	    		for (SearchHit hit : hits) {
	    			retVal.add(Integer.parseInt(hit.getSourceAsMap().get(DOCUMENT_RESULT_FIELD).toString()));
	    		}
	    		return retVal;
	    	} else {
	    		return new ArrayList<Integer>();
	    	}
		} catch (IOException e) {
			LOGGER.warn("cannot query elasticsearch: ", e);
			return new ArrayList<Integer>();
		}
    }

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
		indexStory(body.getFrom().toString(), body.getStory());
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
		return searchStories(keywords);
	}

}
