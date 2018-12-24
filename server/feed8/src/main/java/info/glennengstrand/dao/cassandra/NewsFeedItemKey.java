package info.glennengstrand.dao;


import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;


import com.datastax.driver.core.DataType;

import org.springframework.data.cassandra.core.cql.Ordering;

@PrimaryKeyClass
public class NewsFeedItemKey implements Serializable {
	@PrimaryKeyColumn(name = "ParticipantID", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private Long participantId;
	@PrimaryKeyColumn(name = "occurred", ordinal = 1, ordering = Ordering.DESCENDING)
	@CassandraType(type = DataType.Name.TIMEUUID)
	private UUID occurred;
	public Long getParticipantId() {
		return participantId;
	}
	public void setParticipantId(Long participantId) {
		this.participantId = participantId;
	}
	public UUID getOccurred() {
		return occurred;
	}
	public void setOccurred(UUID occurred) {
		this.occurred = occurred;
	}
}
