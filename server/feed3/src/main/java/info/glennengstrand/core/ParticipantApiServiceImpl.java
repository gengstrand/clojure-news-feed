package info.glennengstrand.core;

import com.google.inject.Inject;

import info.glennengstrand.api.Participant;
import info.glennengstrand.db.ParticipantDAO;
import info.glennengstrand.db.RedisCache;
import info.glennengstrand.resources.ParticipantApi.ParticipantApiService;
import redis.clients.jedis.JedisPool;

public class ParticipantApiServiceImpl implements ParticipantApiService {

	private final ParticipantDAO dao;
	private final RedisCache<Participant> cache;
	
	@Inject
	public ParticipantApiServiceImpl(ParticipantDAO dao, JedisPool pool) {
		this.dao = dao;
		cache = new RedisCache<Participant>(Participant.class, pool);
	}
	
	@Override
	public Participant addParticipant(Participant body) {
		return new Participant.ParticipantBuilder()
				.withId(dao.upsertParticipant(body.getName()))
				.withName(body.getName())
				.build();
	}

	@Override
	public Participant getParticipant(Long id) {
		return cache.get(id, () -> dao.fetchParticipant(id));
	}

}
