package io.swagger.configuration;

import org.apache.http.HttpHost;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;

@Configuration
public class ElasticSearchConfiguration {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ElasticSearchConfiguration.class.getCanonicalName());

    @Value("${elasticsearch.host}")
    private String host;
    
    @Value("${elasticsearch.pool}")
    private int pool;

    private int port = 9200;
    
    @Bean
    public RestHighLevelClient client() {
    	return new RestHighLevelClient(RestClient.builder(new HttpHost(host, port)).setHttpClientConfigCallback(new OverrideConnectionPool()));
    }
    
    class OverrideConnectionPool implements HttpClientConfigCallback {

		@Override
		public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
			httpClientBuilder.setMaxConnTotal(pool);
			return httpClientBuilder;
		}
    	
    }
}
