package io.swagger.configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "info.glennengstrand.dao.elasticsearch")
public class ElasticSearchConfiguration {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ElasticSearchConfiguration.class.getCanonicalName());

    @Value("${elasticsearch.host}")
    private String host;

    private int port = 9200;
    
    @Bean
    public Client client() {
    	System.setProperty("es.set.netty.runtime.available.processors", "false");
    	Settings s = Settings.builder()
    			.put("cluster.name", "elasticsearch")
    			.put("client.transport.sniff", true)
    			.build();
    	TransportClient retVal = new PreBuiltTransportClient(s);
        try {
			retVal.addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
		} catch (UnknownHostException e) {
			LOGGER.warn("cannot get address of elastic search host: ", e);
		}
        return retVal;
    }
}
