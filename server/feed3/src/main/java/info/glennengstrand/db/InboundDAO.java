package info.glennengstrand.db;

import java.util.List;

import org.joda.time.DateTime;

import com.datastax.driver.core.Session;

import info.glennengstrand.NewsFeedConfiguration;
import info.glennengstrand.api.Inbound;

public class InboundDAO extends CassandraDAO<Inbound> {
	
	private static final String OCCURRED_COLUMN = "occured";
	private static final String SUBJECT_COLUMN = "subject";
	private static final String STORY_COLUMN = "story";
	private static final String FROM_PARTICIPANT_ID_COLUMN = "fromparticipantid";

	@Override
	protected String cql(DataOperation op) {
		String retVal = null;
		switch(op) {
		case LOAD:
			retVal = "select dateOf(occurred) as " + OCCURRED_COLUMN + ", " + FROM_PARTICIPANT_ID_COLUMN + ", " + SUBJECT_COLUMN + ", " + STORY_COLUMN + " from Inbound where participantid = ? order by occurred desc";
			break;
		case SAVE:
			retVal = "insert into Inbound (ParticipantID, " + FROM_PARTICIPANT_ID_COLUMN + ", " + OCCURRED_COLUMN + ", " + SUBJECT_COLUMN + ", " + STORY_COLUMN + ") values (?, ?, now(), ?, ?) using ttl 7776000";
			break;
		}
		return retVal;
	}
	
	public void create(Inbound value) {
		upsert(b -> {
			b.setLong(0, value.getTo());
			b.setLong(1,  value.getFrom());
			b.setString(2, value.getSubject());
			b.setString(3,  value.getStory());
		});
	}
	
	public List<Inbound> fetch(long id) {
		return fetchMulti(b -> {
			b.setLong(0, id);
		}, r -> {
			return new Inbound.InboundBuilder()
					.withFrom(r.getLong(FROM_PARTICIPANT_ID_COLUMN))
					.withTo(id)
					.withOccurred(new DateTime(r.getDate(OCCURRED_COLUMN)))
					.withSubject(r.getString(SUBJECT_COLUMN))
					.withStory(r.getString(STORY_COLUMN))
					.build();
		});
	}

	public InboundDAO(Session session, NewsFeedConfiguration config) {
		super(session, config);
	}
}
