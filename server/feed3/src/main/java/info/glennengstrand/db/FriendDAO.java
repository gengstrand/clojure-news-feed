package info.glennengstrand.db;

import java.util.List;
import java.util.stream.Collectors;

import org.skife.jdbi.v2.DBI;

import com.google.inject.Inject;

import info.glennengstrand.api.Friend;

public class FriendDAO extends RelationalDAO<Friend> {

	public List<Friend> fetchFriend(Long id) {
		info.glennengstrand.db.FriendMapper mapper = new info.glennengstrand.db.FriendMapper(id);
		List<Friend> retVal = fetchMulti("call FetchFriends(:id)", mapper, q -> q.bind("id", id));
		return retVal.stream().distinct().collect(Collectors.toList());
	}
	
	public long upsertFriend(Long from, Long to) {
		return upsert("call UpsertFriends(:from, :to)", q -> {
			q.bind("from", from);
			q.bind("to", to);
		});
	}

	@Inject
	public FriendDAO(DBI dbi) {
		super(dbi);
	}
}
