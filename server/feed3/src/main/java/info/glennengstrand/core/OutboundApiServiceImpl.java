package info.glennengstrand.core;

import java.util.List;

import com.google.inject.Inject;

import info.glennengstrand.api.Friend;
import info.glennengstrand.api.Inbound;
import info.glennengstrand.api.Outbound;
import info.glennengstrand.db.InboundDAO;
import info.glennengstrand.db.OutboundDAO;
import info.glennengstrand.resources.FriendApi.FriendApiService;
import info.glennengstrand.resources.OutboundApi.OutboundApiService;

public class OutboundApiServiceImpl implements OutboundApiService {

	private final OutboundDAO outdao;
	private final InboundDAO indao;
	private final FriendApiService friendService;
	
	@Override
	public Outbound addOutbound(Outbound body) {
		for (Friend friend : friendService.getFriend(body.getId())) {
			Inbound in = new Inbound.InboundBuilder()
					.withFrom(body.getId())
					.withTo(friend.getTo())
					.withOccurred(body.getOccurred())
					.withSubject(body.getSubject())
					.withStory(body.getStory())
					.build();
			indao.create(in);
		}
		outdao.create(body);
		return body;
	}

	@Override
	public List<Outbound> getOutbound(Long id) {
		return outdao.fetch(id);
	}

	@Override
	public List<Outbound> searchOutbound(String keywords) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Inject
	public OutboundApiServiceImpl(OutboundDAO outdao, InboundDAO indao, FriendApiService friendService) {
		this.outdao = outdao;
		this.indao = indao;
		this.friendService = friendService;
	}

}
