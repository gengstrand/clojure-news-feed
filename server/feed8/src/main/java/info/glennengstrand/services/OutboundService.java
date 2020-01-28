package info.glennengstrand.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import info.glennengstrand.resources.OutboundApi;
import info.glennengstrand.util.Link;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;


@Service
public class OutboundService implements OutboundApi {

    private static Logger LOGGER = LoggerFactory.getLogger(OutboundService.class.getCanonicalName());
    public static final String DOCUMENT_INDEX = "feed";
    public static final String DOCUMENT_TYPE = "stories";
    public static final int MAX_SEARCH_RESULTS = 1000;
    public static final String DOCUMENT_SEARCH_FIELD = "story";
    public static final String DOCUMENT_RESULT_FIELD = "sender";
    
    @Autowired
    private RestHighLevelClient esClient;
    
    private List<Long> searchStories(String keywords) {
    	SearchRequest req = new SearchRequest(DOCUMENT_INDEX).types(DOCUMENT_TYPE);
    	SearchSourceBuilder builder = new SearchSourceBuilder().size(MAX_SEARCH_RESULTS);
    	builder.query(QueryBuilders.termQuery(DOCUMENT_SEARCH_FIELD, keywords));
    	req.source(builder);
		try {
			SearchResponse resp = esClient.search(req);
	    	if (resp.status() == RestStatus.OK) {
	    		SearchHit[] hits = resp.getHits().getHits();
	    		List<Long> retVal = new ArrayList<>();
	    		for (SearchHit hit : hits) {
	    			retVal.add(Long.parseLong(hit.getSourceAsMap().get(DOCUMENT_RESULT_FIELD).toString()));
	    		}
	    		return retVal;
	    	} else {
	    		return Collections.emptyList();
	    	}
		} catch (IOException e) {
			LOGGER.warn("cannot query elasticsearch: ", e);
			return Collections.emptyList();
		}
    }


	@Override
	public List<String> searchOutbound(String keywords) {
		return searchStories(keywords).stream().map(p -> Link.toLink(p)).collect(Collectors.toList());
	}

}
