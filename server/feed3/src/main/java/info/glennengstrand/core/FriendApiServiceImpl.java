package info.glennengstrand.core;

import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;

import info.glennengstrand.NewsFeedConfiguration;
import info.glennengstrand.NewsFeedModule.FriendCache;
import info.glennengstrand.api.Friend;
import info.glennengstrand.db.FriendDAO;
import info.glennengstrand.resources.FriendApi.FriendApiService;

public class FriendApiServiceImpl implements FriendApiService {

	private static final String ENTITY = "friends";
	private final FriendDAO dao;
	private final FriendCache cache;
	private final MessageLogger<Long> logger;
	
	@Inject 
	public FriendApiServiceImpl(FriendDAO dao, FriendCache cache, MessageLogger<Long> logger) {
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
		cache.invalidate(body.getFrom());
		cache.invalidate(body.getTo());
		logger.log(ENTITY, MessageLogger.LogOperation.POST, System.currentTimeMillis() - before);
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
		logger.log(ENTITY, MessageLogger.LogOperation.GET, System.currentTimeMillis() - before);
		return retVal;
	}

}
