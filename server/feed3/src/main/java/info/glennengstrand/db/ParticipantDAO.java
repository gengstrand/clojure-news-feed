package info.glennengstrand.db;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import info.glennengstrand.api.Participant;

@RegisterMapper(ParticipantMapper.class)
public interface ParticipantDAO {

	@SqlQuery("call FetchParticipant(:id)")
	Participant fetchParticipant(@Bind("id") Long id);
	
	@SqlQuery("call UpsertParticipant(:moniker)")
	long upsertParticipant(@Bind("moniker") String name);
	
}
