package io.swagger.configuration;

import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "info.glennengstrand.dao.elasticsearch")
public class ElasticSearchConfiguration {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ElasticSearchConfiguration.class.getCanonicalName());

    @Value("${elasticsearch.host}")
    private String host;

    private int port = 9200;
    
    @Bean
    RestHighLevelClient client() {

      ClientConfiguration clientConfiguration = ClientConfiguration.builder() 
        .connectedTo(host.concat(":").concat(Integer.toString(port)))
        .build();

      return RestClients.create(clientConfiguration).rest(); 
    }
}
