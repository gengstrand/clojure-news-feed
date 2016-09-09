package info.glennengstrand.db;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import info.glennengstrand.api.Friend;

@RegisterMapper(FriendMapper.class)
public interface FriendDAO {

	@SqlQuery("call FetchFriends(:id)")
	List<Friend> fetchFriend(@Bind("id") Long id);
	
	@SqlQuery("call UpsertFriends(:from, :to)")
	long upsertFriend(@Bind("from") Long from, @Bind("to") Long to);

}
