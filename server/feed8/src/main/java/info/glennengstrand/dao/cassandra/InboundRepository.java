package info.glennengstrand.dao.cassandra;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InboundRepository extends CassandraRepository<Inbound, NewsFeedItemKey> {
	@Query("select Occurred, FromParticipantID, Subject, Story from Inbound where participantID = ?0 order by Occurred desc")
	Stream<Inbound> findByNewsFeedItemKey_ParticipantId(final Integer participantId);
}
