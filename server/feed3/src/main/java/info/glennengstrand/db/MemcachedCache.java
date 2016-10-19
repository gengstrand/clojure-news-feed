package info.glennengstrand.db;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.glennengstrand.NewsFeedConfiguration;
import net.spy.memcached.MemcachedClient;

public class MemcachedCache<T> extends Cache<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MemcachedCache.class);
	
	private final NewsFeedConfiguration config;
	
	private MemcachedClient client = null;
	
	private MemcachedClient getClient() throws IOException {
		if (client == null) {
			synchronized(LOGGER) {
				if (client == null) {
					client = new MemcachedClient(new InetSocketAddress(config.getCacheHost(), config.getCachePort()));
				}
			}
		}
		return client;
	}
	
	public MemcachedCache(Class<T> serializationType, NewsFeedConfiguration config) {
		super(serializationType);
		this.config = config;
	}
	@Override
	public T get(Long id, Supplier<T> loader) {
		T retVal = null;
		String key = convertIdToKey(id);
		MemcachedClient c;
		try {
			c = getClient();
			if (c != null) {
				try {
					Object or = c.get(key);
					if (or == null) {
						LOGGER.debug(String.format("Cache miss on %s so attempting to load the item.", key));
						retVal = loader.get();
						if (retVal != null) {
							c.set(key, config.getCacheTimeToLive(), serialize(retVal));
						} else {
							LOGGER.debug(String.format("Could not load %s either.", key));
						}
					} else {
						String result = or.toString();
						retVal = hydrate(result);
					}
				} catch (Exception e) {
					LOGGER.warn(String.format("Error while attempting to access %s and now will attempt to load the object.", key), e);
					retVal = loader.get();
					try {
						c.set(key, config.getCacheTimeToLive(), serialize(retVal));
					} catch (Exception ie) {
						LOGGER.error(String.format("Cannot serialize %s and save it to Redis: ", key), ie);
					}
				}
			} else {
				LOGGER.error("Cannot access Jedis.");
				retVal = loader.get();
			}
		} catch (IOException e1) {
			LOGGER.warn("cannot obtain memcached connection: ", e1);
		}

		return retVal;
	}
	@Override
	public List<T> getMulti(Long id, Supplier<List<T>> loader) {
		List<T> retVal = null;
		MemcachedClient c;
		try {
			c = getClient();
			String key = convertIdToKey(id);
			if (c != null) {
				try {
					Object or = c.get(key);
					if (or == null) {
						LOGGER.debug(String.format("Cache miss on %s so attempting to load the item.", key));
						retVal = loader.get();
						if (retVal != null) {
							c.set(key, config.getCacheTimeToLive(), serializeMulti(retVal));
						} else {
							LOGGER.debug(String.format("Could not load %s either.", key));
						}
					} else {
						String result = or.toString();
						LOGGER.debug(result);
						retVal = hydrateMulti(result);
					}
				} catch (Exception e) {
					LOGGER.warn(String.format("Error while attempting to access %s and now will attempt to load the object.", key), e);
					retVal = loader.get();
					try {
						c.set(key, config.getCacheTimeToLive(), serializeMulti(retVal));
					} catch (Exception ie) {
						LOGGER.error(String.format("Cannot serialize %s and save it to Memcached: ", key), ie);
					}
				}
			} else {
				LOGGER.error("Cannot access memcached.");
				retVal = loader.get();
			}
		} catch (IOException e1) {
			LOGGER.warn("Cannot acquire a connection to memcached: ", e1);
			retVal = loader.get();
		}

		return retVal;
	}
	@Override
	public void invalidate(Long id) {
		try {
			MemcachedClient c = getClient();
			String key = convertIdToKey(id);
			if (c != null) {
				c.delete(key);
			} else {
				LOGGER.error("Cannot access memcached.");
			}
		} catch (Exception e) {
			LOGGER.warn("Cannot delete cache: ", e);
		}
	}

}
