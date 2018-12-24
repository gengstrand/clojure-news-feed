package info.glennengstrand.dao.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

}
