package info.glennengstrand.db;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.util.LongColumnMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.List;
import java.util.function.Consumer;

public abstract class RelationalDAO<E> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RelationalDAO.class);
	private final DBI dbi;

	protected E fetchSingle(String sql, ResultSetMapper<E> mapper, Consumer<Query<Map<String, Object>>> binder) {
		E retVal = null;
		try (Handle h = dbi.open()) {
			Query<Map<String, Object>> q = h.createQuery(sql);
			binder.accept(q);
			Query<E> result = q.map(mapper);
			retVal = result.first();
		} catch (Exception e) {
			LOGGER.error(String.format("encountered error while attempting to run %s: ", sql), e);
		} 
		return retVal;
	}
	
	protected List<E> fetchMulti(String sql, ResultSetMapper<E> mapper, Consumer<Query<Map<String, Object>>> binder) {
		List<E> retVal = null;
		try (Handle h = dbi.open()) {
			Query<Map<String, Object>> q = h.createQuery(sql);
			binder.accept(q);
			Query<E> result = q.map(mapper);
			retVal = result.list();
		} catch (Exception e) {
			LOGGER.error(String.format("encountered error while attempting to run %s: ", sql), e);
		} 
		return retVal;
	}
	
	protected long upsert(String sql, Consumer<Query<Map<String, Object>>> binder) {
		long retVal = 0l;
		try (Handle h = dbi.open()) {
			Query<Map<String, Object>> q = h.createQuery(sql);
			binder.accept(q);
			Query<Long> result = q.map(LongColumnMapper.PRIMITIVE);
			retVal = result.first();
		} catch (Exception e) {
			LOGGER.error(String.format("encountered error while attempting to run %s: ", sql), e);
		} 
		return retVal;
	}
	
	public RelationalDAO(DBI dbi) {
		this.dbi = dbi;
	}
}
