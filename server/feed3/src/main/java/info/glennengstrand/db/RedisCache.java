package info.glennengstrand.db;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommand.Setter;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

import info.glennengstrand.NewsFeedConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import rx.Observable;

public class RedisCache<T> extends Cache<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);
	
	private final NewsFeedConfiguration config;
	private JedisPool jedis = null;
	private Setter setter = null;
	
	private Setter getSetter() {
		if (setter == null) {
			synchronized(LOGGER) {
				if (setter == null) {
					setter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(RedisCache.class.getCanonicalName()))
							.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
									.withCircuitBreakerEnabled(true)
									.withCircuitBreakerErrorThresholdPercentage(config.getCircuitBreakerErrorThreshold())
									.withCircuitBreakerSleepWindowInMilliseconds(config.getCircuitBreakerSleepWindow()));				}
			}
		}
		return setter;
	}
	
	private JedisPool getPool() {
		if (jedis == null) {
			synchronized(LOGGER) {
				if (jedis == null) {
			    	JedisPoolConfig cacheConfig = new JedisPoolConfig();
			    	cacheConfig.setMaxTotal(config.getCachePoolSize());
			    	cacheConfig.setBlockWhenExhausted(false);
			    	jedis = new JedisPool(cacheConfig, config.getCacheHost(), config.getCachePort(), config.getCacheTimeout());
				}
			}
		}
		return jedis;
	}

	
	private Jedis getConnection() {
		Jedis retVal = null;
		try {
			retVal = getPool().getResource();
		} catch (Exception e) {
			LOGGER.warn("Cannot obtain cache connection but will attempt to recover: ", e);
			jedis.close();
			jedis = null;
			retVal = getPool().getResource();
		}
		return retVal;
	}
	
	@Override
	public T get(Long id, Supplier<T> loader) {
		T retVal = null;
		try {
			retVal = new GetCommand(id, loader).run();		
		} catch (Exception e) {
			LOGGER.warn("Cannot fetch cache: ", e);
			try {
				retVal = loader.get();
			} catch (Exception ie) {
				LOGGER.warn("cannot fetch from db: ", ie);
			}
		}
		return retVal;
	}

	@Override
	public List<T> getMulti(Long id, Supplier<List<T>> loader) {
		List<T> retVal = null;
		try {
			retVal = new GetMulti(id, loader).run();		
		} catch (Exception e) {
			LOGGER.warn("Cannot fetch cache: ", e);
			try {
				retVal = loader.get();
			} catch (Exception ie) {
				retVal = Collections.EMPTY_LIST;
				LOGGER.warn("Cannot fetch from db: ", ie);
			}
		}
		return retVal;
	}	
	
	@Override
	public void invalidate(Long id) {
		Jedis j = null;
		try {
			j = getConnection();
			String key = convertIdToKey(id);
			if (j != null) {
				j.del(key);
			} else {
				LOGGER.error("Cannot access Jedis.");
			}
		} catch (Exception e) {
			LOGGER.warn("Cannot delete cache: ", e);
		} finally {
			if (j != null) {
				j.close();
			}
		}
		
	}
	
	public RedisCache(Class<T> serializationType, NewsFeedConfiguration config) {
		this(serializationType, config, null);
	}

	public RedisCache(Class<T> serializationType, NewsFeedConfiguration config, JedisPool jedis) {
		super(serializationType);
		this.config = config;
		this.jedis = jedis;
	}

	class GetCommand extends HystrixCommand<T> {
		protected final Long id;
		protected final Supplier<T> loader;
		
		public GetCommand(Long id, Supplier<T> loader) {
			super(getSetter());
			this.id = id;
			this.loader = loader;
		}

		@Override
		protected T run() throws Exception {
			T retVal = null;
			String key = convertIdToKey(id);
			Jedis j = getConnection();
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
				} finally {
					j.close();
				}
			} else {
				LOGGER.error("Cannot access Jedis.");
				retVal = loader.get();
			}
			return retVal;
		}
	}
	
	class GetMulti extends HystrixCommand<List<T>> {
		
		protected final Long id;
		protected final Supplier<List<T>> loader;

		public GetMulti(Long id, Supplier<List<T>> loader) {
			super(getSetter());
			this.id = id;
			this.loader = loader;
		}

		@Override
		protected List<T> run() throws Exception {
			List<T> retVal = null;
			Jedis j = getConnection();
			String key = convertIdToKey(id);
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
						LOGGER.debug(result);
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
				} finally {
					j.close();
				}
			} else {
				LOGGER.error("Cannot access Jedis.");
				retVal = loader.get();
			}
			return retVal;
		}

	}

}
