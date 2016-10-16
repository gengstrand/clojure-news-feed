package info.glennengstrand.core;

import com.google.inject.Inject;

import info.glennengstrand.NewsFeedConfiguration;
import info.glennengstrand.api.Participant;
import info.glennengstrand.db.ParticipantDAO;
import info.glennengstrand.db.RedisCache;
import info.glennengstrand.resources.ParticipantApi.ParticipantApiService;
import redis.clients.jedis.JedisPool;

public class ParticipantApiServiceImpl implements ParticipantApiService {
	
	private static final String ENTITY = "Participant";

	private final ParticipantDAO dao;
	private final RedisCache<Participant> cache;
	private final MessageLogger<Long> logger;
	
	@Inject
	public ParticipantApiServiceImpl(ParticipantDAO dao, NewsFeedConfiguration config, MessageLogger<Long> logger) {
		this.dao = dao;
		cache = new RedisCache<Participant>(Participant.class, config);
		this.logger = logger;
	}

	public ParticipantApiServiceImpl(ParticipantDAO dao, NewsFeedConfiguration config, MessageLogger<Long> logger, RedisCache<Participant> cache) {
		this.dao = dao;
		this.cache = cache;
		this.logger = logger;
	}
	
	@Override
	public Participant addParticipant(Participant body) {
		long before = System.currentTimeMillis();
		Participant retVal = new Participant.ParticipantBuilder()
				.withId(dao.upsertParticipant(body.getName()))
				.withName(body.getName())
				.build();
		logger.log(ENTITY, MessageLogger.LogOperation.ADD, System.currentTimeMillis()- before);
		return retVal;
	}

	@Override
	public Participant getParticipant(Long id) {
		long before = System.currentTimeMillis();
		Participant retVal =  cache.get(id, () -> dao.fetchParticipant(id));
		logger.log(ENTITY, MessageLogger.LogOperation.GET, System.currentTimeMillis()- before);
		return retVal;
	}

}
