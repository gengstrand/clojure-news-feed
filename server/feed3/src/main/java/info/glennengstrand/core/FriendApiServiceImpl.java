package info.glennengstrand.core;

import java.util.List;

import com.google.inject.Inject;

import info.glennengstrand.api.Friend;
import info.glennengstrand.db.FriendDAO;
import info.glennengstrand.resources.FriendApi.FriendApiService;

public class FriendApiServiceImpl implements FriendApiService {

	private final FriendDAO dao;
	
	@Inject 
	public FriendApiServiceImpl(FriendDAO dao) {
		this.dao = dao;
	}
	
	@Override
	public Friend addFriend(Friend body) {
		List<Friend> results = getFriend(dao.upsertFriend(body.getFrom(),  body.getTo()));
		return (results != null) && !results.isEmpty() ? results.get(0) : null;
	}

	@Override
	public List<Friend> getFriend(Long id) {
		return dao.fetchFriend(id);
	}

}
