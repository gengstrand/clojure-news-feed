package info.glennengstrand.dao.cassandra;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboundRepository extends CassandraRepository<Inbound, NewsFeedItemKey> {
	List<Inbound> findByParticipantId(final Long participantId);
}
