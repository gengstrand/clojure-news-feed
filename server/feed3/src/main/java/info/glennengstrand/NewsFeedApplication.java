package info.glennengstrand;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.hubspot.dropwizard.guice.GuiceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.glennengstrand.resources.ParticipantApi;
import io.dropwizard.Application;
import io.dropwizard.jersey.params.LongParam;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class NewsFeedApplication extends Application<NewsFeedConfiguration> {

	public static final String SEARCH_OPERATION = "search";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NewsFeedApplication.class);
	
	private Module guiceModule = null;

    public static void main(final String[] args) throws Exception {
        new NewsFeedApplication().run(args);
    }

    @Override
    public String getName() {
        return "NewsFeed";
    }

    @Override
    public void initialize(final Bootstrap<NewsFeedConfiguration> bootstrap) {
    	
        GuiceBundle<NewsFeedConfiguration> guiceBundle = GuiceBundle.<NewsFeedConfiguration>newBuilder()
        	      .addModule(new NewsFeedModule())
        	      .enableAutoConfig(getClass().getPackage().getName())
        	      .setConfigClass(NewsFeedConfiguration.class)
        	      .build();

        	    bootstrap.addBundle(guiceBundle);
        	    
    }

    @Override
    public void run(final NewsFeedConfiguration configuration, final Environment environment) {

    }

}
