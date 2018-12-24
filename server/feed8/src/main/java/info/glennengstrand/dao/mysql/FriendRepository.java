package info.glennengstrand.dao.mysql;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
	List<Friend> findByFromParticipantId(final Long fromParticipantId);
}