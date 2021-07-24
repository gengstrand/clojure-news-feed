package info.glennengstrand.db;

import java.util.List;

import org.joda.time.DateTime;

import com.datastax.driver.core.Session;
import com.google.inject.Inject;

import info.glennengstrand.NewsFeedConfiguration;
import info.glennengstrand.api.Outbound;
import info.glennengstrand.util.Link;

public class OutboundDAO extends CassandraDAO<Outbound> {
        
        private static final String OCCURRED_COLUMN = "Occurred";
        private static final String SUBJECT_COLUMN = "Subject";
        private static final String STORY_COLUMN = "Story";

        @Override
        protected String cql(DataOperation op) {
                String retVal = null;
                switch(op) {
                case LOAD:
                        retVal = "select toTimestamp(occurred) as " + OCCURRED_COLUMN + ", " + SUBJECT_COLUMN + ", " + STORY_COLUMN + " from Outbound where participantid = ? order by occurred desc";
                        break;
                case SAVE:
                        retVal = "insert into Outbound (ParticipantID, " + OCCURRED_COLUMN + ", " + SUBJECT_COLUMN + ", " + STORY_COLUMN + ") values (?, now(), ?, ?) using ttl 7776000";
                        break;
                }
                return retVal;
        }
        
        public void create(Outbound value) {
                upsert(b -> {
                        b.setInt(0, Link.extractId(value.getFrom()).intValue());
                        b.setString(1, value.getSubject());
                        b.setString(2,  value.getStory());
                });
        }
        
        public List<Outbound> fetch(long id) {
                return fetchMulti(b -> {
                        b.setInt(0, new Long(id).intValue());
                }, r -> {
                        return new Outbound.OutboundBuilder()
                                        .withFrom(Link.toLink(id))
                                        .withOccurred(DATE_FORMATTER.print(new DateTime(r.getDate(OCCURRED_COLUMN))))
                                        .withSubject(r.getString(SUBJECT_COLUMN))
                                        .withStory(r.getString(STORY_COLUMN))
                                        .build();
                });
        }

        @Inject
        public OutboundDAO(Session session, NewsFeedConfiguration config) {
                super(session, config);
        }
}

