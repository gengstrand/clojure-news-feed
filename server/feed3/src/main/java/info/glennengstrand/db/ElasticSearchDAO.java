package info.glennengstrand.db;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ElasticSearchDAO extends SearchDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchDAO.class);

	private final URI elasticSearchHost;
	private final String index;
	private final String mapping;
	private final Client client = ClientBuilder.newClient();
	private final ObjectMapper mapper = new ObjectMapper();
	
	private void sendSingleDocumentToElasticSearch(UpsertRequest doc) {
		Response response = client.target(elasticSearchHost) 
				.path(String.format("%s/%s/%s", index, mapping, doc.getId()))
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(doc, MediaType.APPLICATION_JSON));
		int status = response.getStatus();
		if (status >= 300) {
			LOGGER.warn(String.format("request %s returned %d - %s", doc.getId(), status, response.readEntity(String.class)));
		}
	}

	@Override
	public void upsert(final UpsertRequest doc) {
		CompletableFuture.runAsync(new Runnable() {

			@Override
			public void run() {
				sendSingleDocumentToElasticSearch(doc);
			}
			
		});
	}
	
	@Override
	public List<Long> find(String keywords) {
		Response response = client.target(elasticSearchHost) 
				.path(String.format("%s/_search", index))
				.queryParam("q", keywords)
				.request(MediaType.APPLICATION_JSON)
				.get();
		int status = response.getStatus();
		Object e = response.getEntity();
		String responseJson = e == null ? null : e.toString();
		if (status >= 300) {
			if (responseJson != null) {
				LOGGER.warn(responseJson);
			} else {
				LOGGER.warn(String.format("Search request for %s returned status %d", keywords, status));
			}
		} else {
			if (responseJson != null) {
				try {
					JsonNode root = mapper.readTree(responseJson);
					if (root != null) {
						List<JsonNode> sender = root.findValues("sender");
						if (sender != null) {
							return sender.stream().map(mapper -> mapper.asLong()).collect(Collectors.toList());
						}
					}
				} catch (JsonProcessingException e1) {
					LOGGER.error("unrecognised response from elasticsearch: ", e1);
				} catch (IOException e1) {
					LOGGER.error("Could not access elasticsearch: ", e1);
				}
			}
		}
		return Collections.emptyList();
	}
	
	public ElasticSearchDAO(String host, int port, String index, String mapping) throws URISyntaxException  {
		this.elasticSearchHost = new URI(String.format("http://%s:%s/", host, new Integer(port).toString()));
		this.index = index;
		this.mapping = mapping;
	}
	
}
