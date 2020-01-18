package info.glennengstrand.core;

import com.google.inject.Inject;

import info.glennengstrand.NewsFeedConfiguration;
import info.glennengstrand.NewsFeedModule.ParticipantCache;
import info.glennengstrand.api.Participant;
import info.glennengstrand.db.Cache;
import info.glennengstrand.db.MemcachedCache;
import info.glennengstrand.db.ParticipantDAO;
import info.glennengstrand.db.RedisCache;
import info.glennengstrand.resources.ParticipantApi.ParticipantApiService;
import redis.clients.jedis.JedisPool;
import info.glennengstrand.util.Link;

public class ParticipantApiServiceImpl implements ParticipantApiService {
	
	private static final String ENTITY = "participant";

	private final ParticipantDAO dao;
	private final ParticipantCache cache;
	private final MessageLogger<Long> logger;
	
	@Inject
	public ParticipantApiServiceImpl(ParticipantDAO dao, ParticipantCache cache, MessageLogger<Long> logger) {
		this.dao = dao;
		this.cache = cache;
		this.logger = logger;
	}
	
	@Override
	public Participant addParticipant(Participant body) {
		long before = System.currentTimeMillis();
		long id = dao.upsertParticipant(body.getName());
		Participant retVal = new Participant.ParticipantBuilder()
				.withId(id)
				.withName(body.getName())
				.withLink(Link.toLink(id))
				.build();
		logger.log(ENTITY, MessageLogger.LogOperation.POST, System.currentTimeMillis() - before);
		return retVal;
	}

	@Override
	public Participant getParticipant(Long id) {
		long before = System.currentTimeMillis();
		Participant retVal =  cache.get(id, () -> dao.fetchParticipant(id));
		logger.log(ENTITY, MessageLogger.LogOperation.GET, System.currentTimeMillis() - before);
		return retVal;
	}

}
