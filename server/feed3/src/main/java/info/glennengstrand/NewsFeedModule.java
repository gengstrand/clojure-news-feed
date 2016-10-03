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
	private DBI dbi = null;
	private JedisPool pool = null;
	private Session session = null;
	private SearchDAO esdao = null;
	private MessageLogger<Long> logger = null;

	@Override
	public void configure(Binder binder) {
		binder.bind(ParticipantApiService.class).to(ParticipantApiServiceImpl.class);
		binder.bind(FriendApiService.class).to(FriendApiServiceImpl.class);
		binder.bind(InboundApiService.class).to(InboundApiServiceImpl.class);
		binder.bind(OutboundApiService.class).to(OutboundApiServiceImpl.class);
	}
	
    @Provides
	public DBI getDbi(NewsFeedConfiguration config, Environment environment) {
    	if (dbi == null) {
    		synchronized(LOGGER) {
    			if (dbi == null) {
    				dbi = factory.build(environment, config.getDataSourceFactory(), "mysql");
    			}
    		}
    	}
		return dbi;
	}
    
    @Provides
    public JedisPool getCache(NewsFeedConfiguration config) {
    	if (pool == null) {
    		synchronized(LOGGER) {
    			if (pool == null) {
    		    	JedisPoolConfig cacheConfig = new JedisPoolConfig();
    		    	cacheConfig.setMaxTotal(config.getCachePoolSize());
    		    	cacheConfig.setBlockWhenExhausted(false);
    		    	pool = new JedisPool(cacheConfig, config.getCacheHost(), config.getCachePort(), config.getCacheTimeout());
    			}
    		}
    	}
    	return pool;
    }
    
    @Provides
    public Session getNoSqlSession(NewsFeedConfiguration config) {
    	if (session == null) {
    		synchronized(LOGGER) {
    			if (session == null) {
    		    	Cluster cluster = Cluster.builder().addContactPoint(config.getNosqlHost()).build();
    		    	session = cluster.connect(config.getNosqlKeyspace());
    			}
    		}
    	}
    	return session;
    }
    
    @Provides
    public SearchDAO getElasticSearch(NewsFeedConfiguration config) {
    	if (esdao == null) {
    		synchronized(LOGGER) {
    			if (esdao == null) {
    		    	try {
    		    		esdao = new ElasticSearchDAO(config.getSearchHost(), config.getSearchPort(), config.getSearchIndex(), config.getSearchMapping());
    				} catch (Exception e) {
    					LOGGER.error("Cannot connect to elastic search: ", e);
    					esdao = new DoNothingSearchDAO();
    				}
    			}
    		}
    	}
    	return esdao;
    }
    
    @Provides
    public MessageLogger<Long> getPerformanceLogger(NewsFeedConfiguration config) {
    	if (logger == null) {
    		synchronized(LOGGER) {
    			if (logger == null) {
    		    	try {
    		    		logger = new KafkaPerformanceLogger(config.getMessageBroker(), config.getMessageTopic());
    		    	} catch (Exception e) {
    		    		LOGGER.error("Cannot connect to Kafka: ", e);
    		    		logger = new MessageLogger.DoNothingMessageLogger();
    		    	}
    			}
    		}
    	}
    	return logger;
    }
    
    public NewsFeedModule() {
    	
    }

}
