package info.glennengstrand.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import info.glennengstrand.api.Friend;
import info.glennengstrand.resources.FriendsApi;
import info.glennengstrand.resources.NotFoundException;
import io.swagger.configuration.RedisConfiguration.FriendRedisTemplate;
import io.swagger.configuration.RedisConfiguration.Friends;
import info.glennengstrand.dao.mysql.FriendRepository;

@Service
public class FriendsService implements FriendsApi {

	@Autowired
	private FriendRepository repository;
	
	@Autowired
	private FriendRedisTemplate template;
	
	@Override
	public Friend addFriend(Friend body) {
		info.glennengstrand.dao.mysql.Friend p = new info.glennengstrand.dao.mysql.Friend();
		p.setFromParticipantId(body.getFrom());
		p.setToParticipantId(body.getTo());
		info.glennengstrand.dao.mysql.Friend retVal = repository.save(p);
		String fk = "Friend::".concat(body.getFrom().toString());
		template.delete(fk);
		String tk = "Friend::".concat(body.getTo().toString());
		template.delete(tk);
		return body.id(retVal.getId());
	}

	@Override
	public List<Friend> getFriend(Long id) {
		String key = "Friend::".concat(id.toString());
		if (template.hasKey(key)) {
			return template.boundValueOps(key).get();
		} else {
			List<info.glennengstrand.dao.mysql.Friend> r = repository.findByFromParticipantId(id);
			if (r.isEmpty()) {
				throw new NotFoundException(404, String.format("no friends for {}", id));
			} else {
				Friends retVal = r.stream().map(dbf -> {
					return new Friend().id(dbf.getId()).from(dbf.getFromParticipantId()).to(dbf.getToParticipantId());
				}).collect(Collectors.toCollection(() -> { return new Friends(); }));
				template.boundValueOps(key).set(retVal);
				return retVal;
			}
		}
	}

}
