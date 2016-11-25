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

	private static final String ENTITY = "outbound";
	private static final Logger LOGGER = LoggerFactory.getLogger(OutboundApiServiceImpl.class);

	private final OutboundDAO outdao;
	private final InboundDAO indao;
	private final FriendApiService friendService;
	private final SearchDAO esdao;
	private final MessageLogger<Long> logger;
	
	@Override
	public Outbound addOutbound(Outbound body) {
		long before = System.currentTimeMillis();
		friendService.getFriend(body.getFrom()).forEach(friend -> {
			Inbound in = new Inbound.InboundBuilder()
					.withFrom(body.getFrom())
					.withTo(friend.getTo())
					.withOccurred(body.getOccurred())
					.withSubject(body.getSubject())
					.withStory(body.getStory())
					.build();
			indao.create(in);
		});
		outdao.create(body);
		SearchDAO.UpsertRequest doc = esdao.getBuilder()
				.withSender(body.getFrom())
				.withStory(body.getStory())
				.build();
		esdao.upsert(doc);
		logger.log(ENTITY, MessageLogger.LogOperation.POST, System.currentTimeMillis() - before);
		return body;
	}

	@Override
	public List<Outbound> getOutbound(Long id) {
		long before = System.currentTimeMillis();
		List<Outbound> retVal = outdao.fetch(id);
		logger.log(ENTITY, MessageLogger.LogOperation.GET, System.currentTimeMillis() - before);
		return retVal;
	}

	@Override
	public List<Long> searchOutbound(String keywords) {
		long before = System.currentTimeMillis();
		List<Long> retVal = esdao.find(keywords);
		logger.log(ENTITY, MessageLogger.LogOperation.SEARCH, System.currentTimeMillis() - before);
		return retVal;
	}
	
	@Inject
	public OutboundApiServiceImpl(OutboundDAO outdao, InboundDAO indao, FriendApiService friendService, SearchDAO esdao, MessageLogger<Long> logger) {
		this.outdao = outdao;
		this.indao = indao;
		this.friendService = friendService;
		this.esdao = esdao;
		this.logger = logger;
	}

}
