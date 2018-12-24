package info.glennengstrand.dao.cassandra;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboundRepository extends CassandraRepository<Outbound, NewsFeedItemKey> {
	List<Outbound> findByParticipantId(final Long participantId);
}
