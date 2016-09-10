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
		
	private DBI dbi = null;
	private final DBIFactory factory = new DBIFactory();

    private DataSourceFactory database = new DataSourceFactory();
    
    private int cachePoolSize = 1;
    private String cacheHost = "localhost";
    private int cachePort = 6379;
    private int cacheTimeout = 60;
	
	public DBI getDbi(Environment environment) {
		if (dbi == null) {
			synchronized(factory) {
				if (dbi == null) {
					dbi = factory.build(environment, getDataSourceFactory(), "mysql");
				}
			}
		}
		return dbi;
	}
	
    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
    
    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
    	database = factory;
    }

    @JsonProperty("cache_pool")
	public int getCachePoolSize() {
		return cachePoolSize;
	}

    @JsonProperty("cache_pool")
	public void setCachePoolSize(int cachePoolSize) {
		this.cachePoolSize = cachePoolSize;
	}

	@JsonProperty("cache_host")
	public String getCacheHost() {
		return cacheHost;
	}

	@JsonProperty("cache_host")
	public void setCacheHost(String cacheHost) {
		this.cacheHost = cacheHost;
	}

	@JsonProperty("cache_port")
	public int getCachePort() {
		return cachePort;
	}

	@JsonProperty("cache_port")
	public void setCachePort(int cachePort) {
		this.cachePort = cachePort;
	}

	@JsonProperty("cache_timeout")
	public int getCacheTimeout() {
		return cacheTimeout;
	}

	@JsonProperty("cache_timeout")
	public void setCacheTimeout(int cacheTimeout) {
		this.cacheTimeout = cacheTimeout;
	}
    
    

}
