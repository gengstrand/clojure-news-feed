package info.glennengstrand.db;

import java.util.List;
import org.skife.jdbi.v2.DBI;

import com.google.inject.Inject;

import info.glennengstrand.api.Friend;

public class FriendDAO extends RelationalDAO<Friend> {

	public List<Friend> fetchFriend(Long id) {
		info.glennengstrand.db.FriendMapper mapper = new info.glennengstrand.db.FriendMapper(id);
		return fetchMulti("call FetchFriends(:id)", mapper, q -> q.bind("id", id));
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
