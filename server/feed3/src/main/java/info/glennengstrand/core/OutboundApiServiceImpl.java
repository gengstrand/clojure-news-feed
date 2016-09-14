package info.glennengstrand.core;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import info.glennengstrand.api.Friend;
import info.glennengstrand.api.Inbound;
import info.glennengstrand.api.Outbound;
import info.glennengstrand.db.SearchDAO;
import info.glennengstrand.db.InboundDAO;
import info.glennengstrand.db.OutboundDAO;
import info.glennengstrand.resources.FriendApi.FriendApiService;
import info.glennengstrand.resources.OutboundApi.OutboundApiService;

public class OutboundApiServiceImpl implements OutboundApiService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutboundApiServiceImpl.class);

	private final OutboundDAO outdao;
	private final InboundDAO indao;
	private final FriendApiService friendService;
	private final SearchDAO esdao;
	
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
		SearchDAO.UpsertRequest doc = esdao.getBuilder()
				.withSender(body.getId())
				.withStory(body.getStory())
				.build();
		esdao.upsert(doc);
		return body;
	}

	@Override
	public List<Outbound> getOutbound(Long id) {
		return outdao.fetch(id);
	}

	@Override
	public List<Long> searchOutbound(String keywords) {
		return esdao.find(keywords);
	}
	
	@Inject
	public OutboundApiServiceImpl(OutboundDAO outdao, InboundDAO indao, FriendApiService friendService, SearchDAO esdao) {
		this.outdao = outdao;
		this.indao = indao;
		this.friendService = friendService;
		this.esdao = esdao;
	}

}
