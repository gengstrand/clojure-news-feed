package info.glennengstrand;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;

import org.skife.jdbi.v2.DBI;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.Provides;

import info.glennengstrand.db.ParticipantDAO;

import org.hibernate.validator.constraints.*;

import javax.validation.Valid;
import javax.validation.constraints.*;

public class NewsFeedConfiguration extends Configuration {
	
	private static final String DATABASE_PROPERTY_NAME = "database";
	private static final String CACHE_POOL_PROPERTY_NAME = "cache_pool";
	private static final String CACHE_HOST_PROPERTY_NAME = "cache_host";
	private static final String CACHE_PORT_PROPERTY_NAME = "cache_port";
	private static final String CACHE_TIMEOUT_PROPERTY_NAME = "cache_timeout";
	private static final String NOSQL_HOST_PROPERTY_NAME = "nosql_host";
	private static final String NOSQL_KEYSPACE_PROPERTY_NAME = "nosql_keyspace";
	private static final String NOSQL_CONSISTENCY_LEVEL_PROPERTY_NAME = "nosql_consistency_level";
		
    private DataSourceFactory database = new DataSourceFactory();
    
    private int cachePoolSize = 1;
    private String cacheHost = "localhost";
    private int cachePort = 6379;
    private int cacheTimeout = 60;
    private String nosqlHost = "localhost";
    private String nosqlKeyspace = "activity";
    private String nosqlConsistencyLevel = "one";
	
    @JsonProperty(DATABASE_PROPERTY_NAME)
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
    
    @JsonProperty(DATABASE_PROPERTY_NAME)
    public void setDataSourceFactory(DataSourceFactory factory) {
    	database = factory;
    }

    @JsonProperty(CACHE_POOL_PROPERTY_NAME)
	public int getCachePoolSize() {
		return cachePoolSize;
	}

    @JsonProperty(CACHE_POOL_PROPERTY_NAME)
	public void setCachePoolSize(int cachePoolSize) {
		this.cachePoolSize = cachePoolSize;
	}

	@JsonProperty(CACHE_HOST_PROPERTY_NAME)
	public String getCacheHost() {
		return cacheHost;
	}

	@JsonProperty(CACHE_HOST_PROPERTY_NAME)
	public void setCacheHost(String cacheHost) {
		this.cacheHost = cacheHost;
	}

	@JsonProperty(CACHE_PORT_PROPERTY_NAME)
	public int getCachePort() {
		return cachePort;
	}

	@JsonProperty(CACHE_PORT_PROPERTY_NAME)
	public void setCachePort(int cachePort) {
		this.cachePort = cachePort;
	}

	@JsonProperty(CACHE_TIMEOUT_PROPERTY_NAME)
	public int getCacheTimeout() {
		return cacheTimeout;
	}

	@JsonProperty(CACHE_TIMEOUT_PROPERTY_NAME)
	public void setCacheTimeout(int cacheTimeout) {
		this.cacheTimeout = cacheTimeout;
	}

	@JsonProperty(NOSQL_HOST_PROPERTY_NAME)
	public String getNosqlHost() {
		return nosqlHost;
	}

	@JsonProperty(NOSQL_HOST_PROPERTY_NAME)
	public void setNosqlHost(String nosqlHost) {
		this.nosqlHost = nosqlHost;
	}

	@JsonProperty(NOSQL_KEYSPACE_PROPERTY_NAME)
	public String getNosqlKeyspace() {
		return nosqlKeyspace;
	}

	@JsonProperty(NOSQL_KEYSPACE_PROPERTY_NAME)
	public void setNosqlKeyspace(String nosqlKeyspace) {
		this.nosqlKeyspace = nosqlKeyspace;
	}

	@JsonProperty(NOSQL_CONSISTENCY_LEVEL_PROPERTY_NAME)
	public String getNosqlConsistencyLevel() {
		return nosqlConsistencyLevel;
	}

	@JsonProperty(NOSQL_CONSISTENCY_LEVEL_PROPERTY_NAME)
	public void setNosqlConsistencyLevel(String nosqlConsistencyLevel) {
		this.nosqlConsistencyLevel = nosqlConsistencyLevel;
	}

}
