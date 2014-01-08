package com.dynamicalsoftware.support;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;

public abstract class Search {

	private static final Logger log = Logger.getLogger(Search.class.getCanonicalName());
	private static final String PRIMARY_KEY_COL_NAME = "id";
	private static final String SENDER_COL_NAME = "sender";
	private static final String CONTENT_COL_NAME = "story";
	
	public static SolrServer server(String baseUrl) {
		return new HttpSolrServer(baseUrl);
	}
	
	public static SolrServer server(String homeFolder, String core) {
	    File home = new File(homeFolder);
	    CoreContainer container = CoreContainer.createAndLoad(homeFolder, new File(home, "solr.xml"));
	    return new EmbeddedSolrServer(container, core);
	}
	
	public static void add(SolrServer server, long senderID, String story) {
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
	
	public static List<Long> results(SolrServer server, String terms, int limit) {
		List<Long> retVal = new ArrayList<Long>();
	    SolrQuery query = new SolrQuery();
	    query.setQuery(terms);
	    query.setRows(limit);
	    try {
			QueryResponse resp = server.query(query);
			for (SolrDocument doc : resp.getResults()) {
				retVal.add((Long)doc.getFieldValue(SENDER_COL_NAME));
			}
		} catch (SolrServerException e) {
			log.error("cannot execute query", e);
		}
		return retVal;
	}
}
