package io.swagger.configuration;

import org.elasticsearch.action.Action;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.threadpool.ThreadPool;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "info.glennengsAbstracttrand.dao.elasticsearch")
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
    
    /*
    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return null; // new ElasticsearchTemplate(new ClientAdaptor(null, null));
    }
    */
    
    class ClientAdaptor extends AbstractClient {
    	
    	final RestHighLevelClient c;

		public ClientAdaptor(Settings settings, ThreadPool threadPool) {
			super(settings, threadPool);
			c = client();
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> void doExecute(
				Action<Request, Response, RequestBuilder> action, Request request, ActionListener<Response> listener) {
//			listener.onResponse(response);
			
		}
    	
    }

}
