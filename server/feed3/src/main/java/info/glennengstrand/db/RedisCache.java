package info.glennengstrand.db;

import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisCache<T> extends Cache<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);
	public final JedisPool jedis;

	@Override
	public T get(Long id, Supplier<T> loader) {
		T retVal = null;
		String key = convertIdToKey(id);
		Jedis j = jedis.getResource();
		if (j != null) {
			try {
				String result = j.get(key);
				if (result == null) {
					LOGGER.debug(String.format("Cache miss on %s so attempting to load the item.", key));
					retVal = loader.get();
					if (retVal != null) {
						j.set(key, serialize(retVal));
					} else {
						LOGGER.debug(String.format("Could not load %s either.", key));
					}
				} else {
					retVal = hydrate(result);
				}
			} catch (Exception e) {
				LOGGER.warn(String.format("Error while attempting to access %s and now will attempt to load the object.", key), e);
				retVal = loader.get();
				try {
					j.set(key, serialize(retVal));
				} catch (Exception ie) {
					LOGGER.error(String.format("Cannot serialize %s and save it to Redis: ", key), ie);
				}
			}
		} else {
			LOGGER.error("Cannot access Jedis.");
			retVal = loader.get();
		}
		return retVal;
	}


	@Override
	public List<T> getMulti(Long id, Supplier<List<T>> loader) {
		List<T> retVal = null;
		String key = convertIdToKey(id);
		Jedis j = jedis.getResource();
		if (j != null) {
			try {
				String result = j.get(key);
				if (result == null) {
					LOGGER.debug(String.format("Cache miss on %s so attempting to load the item.", key));
					retVal = loader.get();
					if (retVal != null) {
						j.set(key, serializeMulti(retVal));
					} else {
						LOGGER.debug(String.format("Could not load %s either.", key));
					}
				} else {
					retVal = hydrateMulti(result);
				}
			} catch (Exception e) {
				LOGGER.warn(String.format("Error while attempting to access %s and now will attempt to load the object.", key), e);
				retVal = loader.get();
				try {
					j.set(key, serializeMulti(retVal));
				} catch (Exception ie) {
					LOGGER.error(String.format("Cannot serialize %s and save it to Redis: ", key), ie);
				}
			}
		} else {
			LOGGER.error("Cannot access Jedis.");
		}
		return retVal;
	}	
	
	@Override
	public void invalidate(Long id) {
		Jedis j = jedis.getResource();
		String key = convertIdToKey(id);
		if (j != null) {
			j.del(key);
		} else {
			LOGGER.error("Cannot access Jedis.");
		}
		
	}
	
	public RedisCache(Class<T> serializationType, JedisPool pool) {
		super(serializationType);
		this.jedis = pool;
	}

}
