package info.glennengstrand;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;  
import com.google.inject.Provides;

import info.glennengstrand.resources.ParticipantApi.ParticipantApiService;
import io.dropwizard.setup.Environment;
import info.glennengstrand.core.ParticipantApiServiceImpl;
import info.glennengstrand.db.FriendDAO;
import info.glennengstrand.db.ParticipantDAO;
import info.glennengstrand.core.FriendApiServiceImpl;
import info.glennengstrand.core.InboundApiServiceImpl;
import info.glennengstrand.resources.InboundApi.InboundApiService;
import info.glennengstrand.resources.FriendApi.FriendApiService;
import info.glennengstrand.resources.OutboundApi.OutboundApiService;
import info.glennengstrand.core.OutboundApiServiceImpl;

import javax.inject.Named;

public class NewsFeedModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(ParticipantApiService.class).to(ParticipantApiServiceImpl.class);
		binder.bind(FriendApiService.class).to(FriendApiServiceImpl.class);
		binder.bind(InboundApiService.class).to(InboundApiServiceImpl.class);
		binder.bind(OutboundApiService.class).to(OutboundApiServiceImpl.class);
	}
	
    @Provides
    public ParticipantDAO getParticipantDAO(NewsFeedConfiguration config, Environment environment) {
    	return config.getDbi(environment).onDemand(ParticipantDAO.class);
    }
	
    @Provides
    public FriendDAO getFriendDAO(NewsFeedConfiguration config, Environment environment) {
    	return config.getDbi(environment).onDemand(FriendDAO.class);
    }
    
    public NewsFeedModule() {
    	
    }

}
