package info.glennengstrand.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import info.glennengstrand.api.Friend;

public class FriendMapper implements ResultSetMapper<Friend> {

	@Override
	public Friend map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new Friend.FriendBuilder()
				.withId(r.getLong("FriendsID"))
				.withFrom((Long)ctx.getAttribute("from"))
				.withTo(r.getLong("ParticipantID"))
				.build();
	}
}
