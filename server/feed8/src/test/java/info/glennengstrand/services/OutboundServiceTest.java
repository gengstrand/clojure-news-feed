package info.glennengstrand.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import org.springframework.test.context.junit4.SpringRunner;
import org.threeten.bp.OffsetDateTime;

import info.glennengstrand.api.Outbound;
import info.glennengstrand.dao.cassandra.InboundRepository;
import info.glennengstrand.dao.cassandra.NewsFeedItemKey;
import info.glennengstrand.dao.cassandra.OutboundRepository;
import info.glennengstrand.dao.mysql.Friend;
import info.glennengstrand.dao.mysql.FriendRepository;
import info.glennengstrand.resources.FriendsApi;
import info.glennengstrand.resources.InboundApi;
import info.glennengstrand.resources.OutboundApi;
import io.swagger.configuration.RedisConfiguration.FriendRedisTemplate;

@RunWith(SpringRunner.class)
public class OutboundServiceTest {

	@TestConfiguration
	static class OutboundServiceImplTestContextConfiguration {
		@Bean
		public OutboundApi outboundService() {
			return new OutboundService();
		}
		@Bean
		public FriendsApi friendsService() {
			return new FriendsService();
		}
		@Bean
		public InboundApi inboundService() {
			return new InboundService();
		}
	}
	
	@Autowired
	private OutboundApi outboundService;
	
	@MockBean
	private OutboundRepository outboundRepository;

	@MockBean
	private FriendRepository friendRepository;
	
	@MockBean
	private InboundRepository inboundRepository;

	@MockBean
	private FriendRedisTemplate friendRedisTemplate;
	
	@MockBean
	private RestHighLevelClient esClient;
	
	private static final Long fromParticipantId = 1L;
	private static final Long toParticipantId = 2L;
	
	@Before
	public void setUp() throws Exception {
		List<Friend> friends = new ArrayList<>();
		Friend f = new Friend();
		f.setId(1l);
		f.setFromParticipantId(fromParticipantId);
		f.setToParticipantId(toParticipantId);
		friends.add(f);
		info.glennengstrand.dao.cassandra.Outbound o = new info.glennengstrand.dao.cassandra.Outbound();
		NewsFeedItemKey k = new NewsFeedItemKey();
		o.setOccured(k.getOccurred());
		Mockito.when(friendRepository.findByFromParticipantId(fromParticipantId)).thenReturn(friends);
		Mockito.when(friendRedisTemplate.hasKey(Mockito.anyString())).thenReturn(false);
		Mockito.when(friendRedisTemplate.boundValueOps(Mockito.anyString())).thenReturn(new FriendRedisOperation());
		Mockito.when(outboundRepository.save(Mockito.any())).thenReturn(o);
	}

	@Test
	public void testAddOutbound() throws IOException {
		Outbound t = new Outbound().from(fromParticipantId).story("test story").subject("test subject").occurred(OffsetDateTime.now());
		outboundService.addOutbound(t);
		Mockito.verify(inboundRepository).save(Mockito.any());
	}

}
