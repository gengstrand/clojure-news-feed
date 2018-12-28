package info.glennengstrand.dao.cassandra;

import java.util.stream.Stream;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboundRepository extends CassandraRepository<Outbound, NewsFeedItemKey> {
	@Query("select Occurred, Subject, Story from Outbound where participantID = ?0 order by Occurred desc")
	Stream<Outbound> findByNewsFeedItemKey_ParticipantId(final Integer participantId);
}
