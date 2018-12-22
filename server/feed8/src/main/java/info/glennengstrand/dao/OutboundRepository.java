package info.glennengstrand.dao;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface OutboundRepository extends CassandraRepository<Outbound, NewsFeedItemKey> {
	List<Outbound> findByParticipantId(final Long participantId);
}
