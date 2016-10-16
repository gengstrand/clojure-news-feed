package info.glennengstrand.core;

import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;

import info.glennengstrand.NewsFeedConfiguration;
import info.glennengstrand.api.Friend;
import info.glennengstrand.api.Participant;
import info.glennengstrand.db.FriendDAO;
import info.glennengstrand.db.RedisCache;
import info.glennengstrand.resources.FriendApi.FriendApiService;
import redis.clients.jedis.JedisPool;

public class FriendApiServiceImpl implements FriendApiService {

	private static final String ENTITY = "Friend";
	private final FriendDAO dao;
	private final RedisCache<Friend> cache;
	private final MessageLogger<Long> logger;
	
	@Inject 
	public FriendApiServiceImpl(FriendDAO dao, NewsFeedConfiguration config, MessageLogger<Long> logger) {
		this.dao = dao;
		cache = new RedisCache<Friend>(Friend.class, config);
		this.logger = logger;
	}

	public FriendApiServiceImpl(FriendDAO dao, NewsFeedConfiguration config, MessageLogger<Long> logger, RedisCache<Friend> cache) {
		this.dao = dao;
		this.cache = cache;
		this.logger = logger;
	}
	
	@Override
	public Friend addFriend(Friend body) {
		long before = System.currentTimeMillis();
		Friend retVal = new Friend.FriendBuilder()
				.withId(dao.upsertFriend(body.getFrom(),  body.getTo()))
				.withFrom(body.getFrom())
				.withTo(body.getTo())
				.build();
		logger.log(ENTITY, MessageLogger.LogOperation.ADD, System.currentTimeMillis()- before);
		return retVal;
	}

	@Override
	public List<Friend> getFriend(Long id) {
		long before = System.currentTimeMillis();
		List<Friend> retVal = cache.getMulti(id, () -> dao.fetchFriend(id));
		if (retVal == null) {
			// mockito doesn't work well inside of lambdas
			retVal = dao.fetchFriend(id);
			if (retVal == null) {
				retVal = Collections.emptyList();
			}
		}
		logger.log(ENTITY, MessageLogger.LogOperation.GET, System.currentTimeMillis()- before);
		return retVal;
	}

}
