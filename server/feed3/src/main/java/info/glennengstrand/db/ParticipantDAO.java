package info.glennengstrand.db;

import org.skife.jdbi.v2.DBI;

import com.google.inject.Inject;

import info.glennengstrand.api.Participant;

public class ParticipantDAO extends RelationalDAO<Participant> {
	
	public Participant fetchParticipant(Long id) {
		return fetchSingle("call FetchParticipant(:id)", new ParticipantMapper(id), q -> q.bind("id", id));
	}
	
	public long upsertParticipant(String name) {
		return upsert("call UpsertParticipant(:moniker)", q -> q.bind("moniker", name));
	}

	@Inject
	public ParticipantDAO(DBI dbi) {
		super(dbi);
	}
	
}
