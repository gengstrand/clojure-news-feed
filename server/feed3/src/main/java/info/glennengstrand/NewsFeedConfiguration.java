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

}
