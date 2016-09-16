package info.glennengstrand;

import com.google.inject.Binder;
import com.google.inject.Module;  
import com.google.inject.Provides;

import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Cluster;

import info.glennengstrand.db.ElasticSearchDAO;
import info.glennengstrand.db.SearchDAO.DoNothingSearchDAO;
import info.glennengstrand.db.SearchDAO;
import info.glennengstrand.core.ParticipantApiServiceImpl;
import info.glennengstrand.core.MessageLogger;
import info.glennengstrand.core.FriendApiServiceImpl;
import info.glennengstrand.core.InboundApiServiceImpl;
import info.glennengstrand.core.KafkaPerformanceLogger;
import info.glennengstrand.core.OutboundApiServiceImpl;
import info.glennengstrand.resources.ParticipantApi.ParticipantApiService;
import info.glennengstrand.resources.InboundApi.InboundApiService;
import info.glennengstrand.resources.FriendApi.FriendApiService;
import info.glennengstrand.resources.OutboundApi.OutboundApiService;

import org.apache.kafka.clients.producer.Producer;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewsFeedModule implements Module {

	private static final Logger LOGGER = LoggerFactory.getLogger(NewsFeedModule.class);

	private final DBIFactory factory = new DBIFactory();

	@Override
	public void configure(Binder binder) {
		binder.bind(ParticipantApiService.class).to(ParticipantApiServiceImpl.class);
		binder.bind(FriendApiService.class).to(FriendApiServiceImpl.class);
		binder.bind(InboundApiService.class).to(InboundApiServiceImpl.class);
		binder.bind(OutboundApiService.class).to(OutboundApiServiceImpl.class);
	}
	
    @Provides
	public DBI getDbi(NewsFeedConfiguration config, Environment environment) {
		return factory.build(environment, config.getDataSourceFactory(), "mysql");
	}
    
    @Provides
    public JedisPool getCache(NewsFeedConfiguration config) {
    	JedisPoolConfig cacheConfig = new JedisPoolConfig();
    	cacheConfig.setMaxTotal(config.getCachePoolSize());
    	cacheConfig.setBlockWhenExhausted(false);
    	return new JedisPool(cacheConfig, config.getCacheHost(), config.getCachePort(), config.getCacheTimeout());
    }
    
    @Provides
    public Session getNoSqlSession(NewsFeedConfiguration config) {
    	Cluster cluster = Cluster.builder().addContactPoint(config.getNosqlHost()).build();
    	return cluster.connect(config.getNosqlKeyspace());
    }
    
    @Provides
    public SearchDAO getElasticSearch(NewsFeedConfiguration config) {
    	SearchDAO retVal = null;
    	try {
			retVal = new ElasticSearchDAO(config.getSearchHost(), config.getSearchPort(), config.getSearchIndex(), config.getSearchMapping());
		} catch (Exception e) {
			LOGGER.error("Cannot connect to elastic search: ", e);
			retVal = new DoNothingSearchDAO();
		}
    	return retVal;
    }
    
    @Provides
    public MessageLogger<Long> getPerformanceLogger(NewsFeedConfiguration config) {
    	MessageLogger<Long> retVal = null;
    	try {
    		retVal = new KafkaPerformanceLogger(config.getMessageBroker(), config.getMessageTopic());
    	} catch (Exception e) {
    		LOGGER.error("Cannot connect to Kafka: ", e);
    		retVal = new MessageLogger.DoNothingMessageLogger();
    	}
    	return retVal;
    }
    
    public NewsFeedModule() {
    	
    }

}
