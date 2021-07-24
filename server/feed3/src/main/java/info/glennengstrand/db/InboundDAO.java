package info.glennengstrand.db;

import java.util.List;

import org.joda.time.DateTime;

import com.datastax.driver.core.Session;
import com.google.inject.Inject;

import info.glennengstrand.NewsFeedConfiguration;
import info.glennengstrand.api.Inbound;
import info.glennengstrand.util.Link;

public class InboundDAO extends CassandraDAO<Inbound> {
        
        private static final String OCCURRED_COLUMN = "Occurred";
        private static final String SUBJECT_COLUMN = "Subject";
        private static final String STORY_COLUMN = "Story";
        private static final String FROM_PARTICIPANT_ID_COLUMN = "FromParticipantID";

        @Override
        protected String cql(DataOperation op) {
                String retVal = null;
                switch(op) {
                case LOAD:
                        retVal = "select toTimestamp(occurred) as " + OCCURRED_COLUMN + ", " + FROM_PARTICIPANT_ID_COLUMN + ", " + SUBJECT_COLUMN + ", " + STORY_COLUMN + " from Inbound where participantid = ? order by occurred desc";
                        break;
                case SAVE:
                        retVal = "insert into Inbound (ParticipantID, " + FROM_PARTICIPANT_ID_COLUMN + ", " + OCCURRED_COLUMN + ", " + SUBJECT_COLUMN + ", " + STORY_COLUMN + ") values (?, ?, now(), ?, ?) using ttl 7776000";
                        break;
                }
                return retVal;
        }
        
        public void create(Inbound value) {
                upsert(b -> {
                        b.setInt(0, Link.extractId(value.getTo()).intValue());
                        b.setInt(1,  Link.extractId(value.getFrom()).intValue());
                        b.setString(2, value.getSubject());
                        b.setString(3,  value.getStory());
                });
        }
        
        public List<Inbound> fetch(long id) {
                return fetchMulti(b -> {
                        b.setInt(0, new Long(id).intValue());
                }, r -> {
                        return new Inbound.InboundBuilder()
                                        .withFrom(Link.toLink(new Integer(r.getInt(FROM_PARTICIPANT_ID_COLUMN)).longValue()))
                                        .withTo(Link.toLink(id))
                                        .withOccurred(DATE_FORMATTER.print(new DateTime(r.getDate(OCCURRED_COLUMN))))
                                        .withSubject(r.getString(SUBJECT_COLUMN))
                                        .withStory(r.getString(STORY_COLUMN))
                                        .build();
                });
        }

        @Inject
        public InboundDAO(Session session, NewsFeedConfiguration config) {
                super(session, config);
        }
}
