package info.glennengstrand.resources;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import info.glennengstrand.api.Friend;
import info.glennengstrand.api.Inbound;
import info.glennengstrand.api.Outbound;
import info.glennengstrand.core.FriendApiServiceImpl;
import info.glennengstrand.core.MessageLogger;
import info.glennengstrand.db.FriendDAO;
import info.glennengstrand.db.InboundDAO;
import info.glennengstrand.resources.FriendApi.FriendApiService;
import redis.clients.jedis.JedisPool;

public abstract class NewsFeedTestBase {

	protected static final Long TEST_ID = 1l;
	protected static final long TEST_FROM = 1l;
	protected static final long TEST_TO = 2l;
	protected static final String TEST_SUBJECT = "test subject";
	protected static final String TEST_STORY = "Mares eat oats and does eat oats and little lambs eat ivey.";
	
	protected JedisPool cache = null;
	protected FriendDAO friendDao = null;
	protected FriendApiService friendApi = null;
	protected Friend friend = null;
	protected List<Friend> friends = new ArrayList<Friend>();
	protected InboundDAO inDao = null;
	protected Inbound inbound = null;
	protected List<Inbound> inFeed = new ArrayList<Inbound>();

	protected void setupFriendSupport() {
    	friend = new Friend.FriendBuilder()
    			.withId(TEST_ID)
    			.withFrom(TEST_FROM)
    			.withTo(TEST_TO)
    			.build();
    	friends.add(friend);
    	cache = mock(JedisPool.class);
    	when(cache.getResource()).thenReturn(null);
    	friendDao = mock(FriendDAO.class);
    	when(friendDao.upsertFriend(any(Long.class), any(Long.class))).thenReturn(TEST_ID);
    	when(friendDao.fetchFriend(any(Long.class))).thenReturn(friends);
    	friendApi = new  FriendApiServiceImpl(friendDao, cache, new MessageLogger.DoNothingMessageLogger());
    }
	
	protected void setupInboundSupport() {
    	inbound = new Inbound.InboundBuilder()
    			.withFrom(TEST_FROM)
    			.withOccurred(new DateTime(System.currentTimeMillis()))
    			.withSubject(TEST_SUBJECT)
    			.withTo(TEST_TO)
    			.withStory(TEST_STORY)
    			.build();
		inFeed.add(inbound);
    	inDao = mock(InboundDAO.class);
    	when(inDao.fetch(any(Long.class))).thenReturn(inFeed);
    }
    
}
