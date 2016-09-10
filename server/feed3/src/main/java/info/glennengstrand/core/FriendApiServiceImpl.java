package info.glennengstrand.core;

import java.util.List;

import com.google.inject.Inject;

import info.glennengstrand.api.Friend;
import info.glennengstrand.api.Participant;
import info.glennengstrand.db.FriendDAO;
import info.glennengstrand.db.RedisCache;
import info.glennengstrand.resources.FriendApi.FriendApiService;
import redis.clients.jedis.JedisPool;

public class FriendApiServiceImpl implements FriendApiService {

	private final FriendDAO dao;
	private final RedisCache<Friend> cache;
	
	@Inject 
	public FriendApiServiceImpl(FriendDAO dao, JedisPool pool) {
		this.dao = dao;
		cache = new RedisCache<Friend>(Friend.class, pool);
	}
	
	@Override
	public Friend addFriend(Friend body) {
		return new Friend.FriendBuilder()
				.withId(dao.upsertFriend(body.getFrom(),  body.getTo()))
				.withFrom(body.getFrom())
				.withTo(body.getTo())
				.build();
	}

	@Override
	public List<Friend> getFriend(Long id) {
		return cache.getMulti(id, () -> dao.fetchFriend(id));
	}

}
