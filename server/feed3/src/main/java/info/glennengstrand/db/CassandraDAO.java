package info.glennengstrand.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormat;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.common.base.Strings;

import info.glennengstrand.NewsFeedConfiguration;
import info.glennengstrand.db.CassandraDAO.DataOperation;

/**
 * data access layer for Cassandra tables
 * @author glenn
 *
 * @param <E> is the entity being saved to or loaded from Cassandra
 */
public abstract class CassandraDAO<E> {

    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
	private final Session session;
	private final ConsistencyLevel level;
	
	private static final Map<String, PreparedStatement> stmts = new HashMap<String, PreparedStatement>();
	private static final Logger LOGGER = LoggerFactory.getLogger(CassandraDAO.class);

	/**
	 * lookup the CQL statement that corresponds to the operation
	 * @param op identifies the operation needing CQL
	 * @return the CQL string to be compiled into a prepared statement
	 */
	protected abstract String cql(DataOperation op);
	
	private String convertOperationToKey(DataOperation op) {
		return this.getClass().getCanonicalName().concat(":").concat(op.name());
	}
	
	private PreparedStatement get(DataOperation op) {
		PreparedStatement retVal = null;
		String key = convertOperationToKey(op);
		if (!stmts.containsKey(key)) {
			synchronized(stmts) {
				if (!stmts.containsKey(key)) {
					String cmd = cql(op);
					if (!Strings.isNullOrEmpty(cmd)) {
						retVal = session.prepare(cmd);
						retVal.setConsistencyLevel(level);
						stmts.put(key, retVal);
					} else {
						throw new NoSuchElementException(String.format("could not find any cql for %s.", key));
					}
				} else {
					retVal = stmts.get(key);
				}				
			}
		} else {
			retVal = stmts.get(key);
		}
		return retVal;
	}
	
	/**
	 * insert or update a row in a cassandra table
	 * @param binder provides the values for the placeholders in the compiled CQL
	 */
	protected void upsert(Consumer<BoundStatement> binder) {
		E retVal = null;
		try {
			BoundStatement b = new BoundStatement(get(DataOperation.SAVE));
			binder.accept(b);
			session.execute(b.bind());
		} catch (Exception e) {
			LOGGER.error(String.format("Was not able to fetch single row for %s: ", DataOperation.SAVE.name()), e);
		}
	}
	
	/**
	 * fetch a wide row of data from a cassandra table
	 * @param binder provides the values for the placeholders in the compiled CQL
	 * @param mapper is responsible for creating entities from resultset data
	 * @return the corresponding list of entity objects
	 */
	protected List<E> fetchMulti(Consumer<BoundStatement> binder, Function<Row,E> mapper) {
		List<E> retVal = new ArrayList<E>();
		try {
			BoundStatement b = new BoundStatement(get(DataOperation.LOAD));
			binder.accept(b);
			ResultSet result = session.execute(b.bind());
			if (result != null) {
				for (Row r : result.all()) {
					retVal.add(mapper.apply(r));
				}
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Was not able to fetch single row for %s: ", DataOperation.LOAD.name()), e);
		}
		return retVal;
	}
	
	public CassandraDAO(Session session, NewsFeedConfiguration config) {
		this.session = session;
		String cl = config.getNosqlConsistencyLevel();
		if ("one".equalsIgnoreCase(cl)) {
			level = ConsistencyLevel.ONE;
		} else if ("quorum".equalsIgnoreCase(cl)) {
			level = ConsistencyLevel.LOCAL_QUORUM;
		} else {
			level = ConsistencyLevel.ANY;
		}
	}
	
	public enum DataOperation {
		LOAD,SAVE;
	}
}
