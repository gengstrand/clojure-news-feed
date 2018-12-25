package io.swagger.configuration;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import info.glennengstrand.api.Participant;
import info.glennengstrand.api.Friend;

@Configuration
public class RedisConfiguration {

	@Value("${redis.host}")
	private String redisHost;
	
	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
	    return new JedisConnectionFactory(new RedisStandaloneConfiguration(redisHost));
	}

	@Bean
	public ParticipantRedisTemplate participantRedisTemplate(JedisConnectionFactory factory) {
	    final ParticipantRedisTemplate template = new ParticipantRedisTemplate();
	    template.setConnectionFactory(factory);
	    template.setKeySerializer(new StringRedisSerializer());
	    template.setValueSerializer(new Jackson2JsonRedisSerializer<Participant>(Participant.class));
	    return template;
	}
	
	@Bean
	public FriendRedisTemplate friendRedisTemplate(JedisConnectionFactory factory) {
	    final FriendRedisTemplate template = new FriendRedisTemplate();
	    template.setConnectionFactory(factory);
	    template.setKeySerializer(new StringRedisSerializer());
	    template.setValueSerializer(new Jackson2JsonRedisSerializer<Friends>(Friends.class));
	    return template;
	}
	
	public static class ParticipantRedisTemplate extends RedisTemplate<String, Participant> {}
	public static class FriendRedisTemplate extends RedisTemplate<String, Friends> {}
	public static class Friends extends ArrayList<Friend> {}
}
