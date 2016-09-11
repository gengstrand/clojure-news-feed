package info.glennengstrand.db;

import java.util.List;

import org.joda.time.DateTime;

import com.datastax.driver.core.Session;

import info.glennengstrand.NewsFeedConfiguration;
import info.glennengstrand.api.Outbound;

public class OutboundDAO extends CassandraDAO<Outbound> {
	
	private static final String OCCURRED_COLUMN = "occured";
	private static final String SUBJECT_COLUMN = "subject";
	private static final String STORY_COLUMN = "story";

	@Override
	protected String cql(DataOperation op) {
		String retVal = null;
		switch(op) {
		case LOAD:
			retVal = "select dateOf(occurred) as " + OCCURRED_COLUMN + ", " + SUBJECT_COLUMN + ", " + STORY_COLUMN + " from Outbound where participantid = ? order by occurred desc";
			break;
		case SAVE:
			retVal = "insert into Outbound (ParticipantID, " + OCCURRED_COLUMN + ", " + SUBJECT_COLUMN + ", " + STORY_COLUMN + ") values (?, now(), ?, ?) using ttl 7776000";
			break;
		}
		return retVal;
	}
	
	public void create(Outbound value) {
		upsert(b -> {
			b.setLong(0, value.getId());
			b.setString(1, value.getSubject());
			b.setString(2,  value.getStory());
		});
	}
	
	public List<Outbound> fetch(long id) {
		return fetchMulti(b -> {
			b.setLong(0, id);
		}, r -> {
			return new Outbound.OutboundBuilder()
					.withId(id)
					.withOccurred(new DateTime(r.getDate(OCCURRED_COLUMN)))
					.withSubject(r.getString(SUBJECT_COLUMN))
					.withStory(r.getString(STORY_COLUMN))
					.build();
		});
	}

	public OutboundDAO(Session session, NewsFeedConfiguration config) {
		super(session, config);
	}
}

