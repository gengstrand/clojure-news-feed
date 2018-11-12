package com.dynamicalsoftware.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;

public abstract class Search {

	private static final Logger log = Logger.getLogger(Search.class.getCanonicalName());
	private static final String PRIMARY_KEY_COL_NAME = "id";
	private static final String SENDER_COL_NAME = "sender";
	private static final String CONTENT_COL_NAME = "story";
	
	public static SolrClient server(String baseUrl) {
		return new HttpSolrClient.Builder()
				.withBaseSolrUrl(baseUrl)
				.build();
	}
	
	public static void add(SolrClient server, long senderID, String story) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField(PRIMARY_KEY_COL_NAME, UUID.randomUUID().toString());
		doc.addField(SENDER_COL_NAME, senderID);
		doc.addField(CONTENT_COL_NAME, story);
		try {
			server.add(doc);
			server.commit(true, true, true);
		} catch (SolrServerException e) {
			log.error("error while searching", e);
		} catch (IOException e) {
			log.error("cannot access search index", e);
		}
	}
	
	public static List<Long> results(SolrClient server, String terms, int limit) {
		List<Long> retVal = new ArrayList<Long>();
		ModifiableSolrParams params = new ModifiableSolrParams();
		params.set("q", terms);
		params.set("rows", limit);
		//SolrQuery query = new SolrQuery();
	    //query.setQuery(terms);
	    //query.setRows(limit);
	    try {
			QueryResponse resp = server.query(params);
			for (SolrDocument doc : resp.getResults()) {
				retVal.add((Long)doc.getFieldValue(SENDER_COL_NAME));
			}
		} catch (Exception e) {
			log.error("cannot execute query", e);
		}
		return retVal;
	}
}
