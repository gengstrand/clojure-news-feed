package info.glennengstrand.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * responsible for accessing a cache
 * @author glenn
 *
 * @param <T> is the type of object to be cached
 */
public abstract class Cache<T> {

	private ObjectMapper mapper = new ObjectMapper();
	private final Class<T> serializationType;
	
	/**
	 * convert the primary key of the item to a cache friendly key
	 * @param id uniquely identifies the item to be accessed
	 * @return a safely namespaced key for the actual cache lookup
	 */
	protected String convertIdToKey(Long id) {
		return serializationType.getSimpleName().concat("::").concat(id.toString());
	}
	
	/**
	 * converts a JSON string into an object 
	 * @param value holds the JSON representation of the string
	 * @return the fully hydrated object
	 * @throws JsonParseException if invalid JSON
	 * @throws JsonMappingException if attributes cannot be found
	 * @throws IOException if cannot access redis
	 */
	protected T hydrate(String value) throws JsonParseException, JsonMappingException, IOException {
		return (T)mapper.readValue(value.getBytes(), serializationType);
	}

	/**
	 * converts a JSON string into an object collection 
	 * @param value holds the JSON representation of the string
	 * @return the fully hydrated object collection
	 * @throws JsonParseException if invalid JSON
	 * @throws JsonMappingException if attributes cannot be found
	 * @throws IOException if cannot access redis
	 */
	protected List<T> hydrateMulti(String value) throws JsonParseException, JsonMappingException, IOException {
		List<T> stc = new ArrayList<T>();
		return (List<T>)mapper.readValue(value.getBytes(), stc.getClass()).stream().map(lhm -> mapper.convertValue(lhm, serializationType)).collect(Collectors.toList());
	}

	/**
	 * converts an object into a JSON string
	 * @param value holds the object to be serialized
	 * @return the string representation of the object in JSON format
	 * @throws JsonProcessingException if cannot parse JSON
	 */
	protected String serialize(T value) throws JsonProcessingException {
		return mapper.writeValueAsString(value);
	}

	/**
	 * converts an object collection into a JSON string
	 * @param value holds the objects to be serialized
	 * @return the string representation of the objects in JSON format
	 * @throws JsonProcessingException if cannot parse JSON
	 */
	protected String serializeMulti(List<T> value) throws JsonProcessingException {
		return mapper.writeValueAsString(value);
	}
	
	/**
	 * fetch an item from the cache
	 * @param id is the primary key for the underlying object
	 * @param loader is responsible for loading the item on cache miss
	 * @return the desired item as identified by the key
	 */
	public abstract T get(Long id, Supplier<T> loader);

	/**
	 * fetch a collection of items from the cache
	 * @param id is the index by which this collection is queried
	 * @param loader is responsible for loading the copllection on cache miss
	 * @return the desired collection as identified by the key
	 */
	public abstract List<T> getMulti(Long id, Supplier<List<T>> loader);
	
	/**
	 * invalidate the item in the cache
	 * @param id is the primary key for the underlying object
	 */
	public abstract void invalidate(Long id);
	
	public Cache(Class<T> serializationType) {
		this.serializationType = serializationType;
	}
}
