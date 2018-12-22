package info.glennengstrand.dao;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface InboundRepository extends CassandraRepository<Inbound, NewsFeedItemKey> {
	List<Inbound> findByParticipantId(final Long participantId);
}
