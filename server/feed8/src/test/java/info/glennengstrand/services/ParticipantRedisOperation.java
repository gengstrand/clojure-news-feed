package info.glennengstrand.services;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;

import io.swagger.configuration.RedisConfiguration;
import info.glennengstrand.api.Participant;

public class ParticipantRedisOperation implements BoundValueOperations<String, Participant> {

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getExpire() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean expire(long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean expireAt(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean persist() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rename(String newKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void set(Participant value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void set(Participant value, long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Boolean setIfAbsent(Participant value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean setIfAbsent(Participant value, long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean setIfPresent(Participant value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean setIfPresent(Participant value, long timeout, TimeUnit unit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Participant get() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Participant getAndSet(Participant value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long increment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long increment(long delta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double increment(double delta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long decrement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long decrement(long delta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer append(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get(long start, long end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(Participant value, long offset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Long size() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisOperations<String, Participant> getOperations() {
		// TODO Auto-generated method stub
		return null;
	}

}
