package info.glennengstrand.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import info.glennengstrand.api.Participant;

public class ParticipantMapper implements ResultSetMapper<Participant> {
	
	private final long id;

	@Override
	public Participant map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new Participant.ParticipantBuilder()
				.withId(id)
				.withName(r.getString("Moniker"))
				.build();
	}
	
	public ParticipantMapper(long id) {
		this.id = id;
	}

}
