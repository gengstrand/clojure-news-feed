package info.glennengstrand.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
	List<Friend> findByFromParticipantId(final Long fromParticipantId);
}