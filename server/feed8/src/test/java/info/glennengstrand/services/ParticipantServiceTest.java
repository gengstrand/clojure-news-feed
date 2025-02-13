package info.glennengstrand.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

import info.glennengstrand.api.Outbound;
import info.glennengstrand.api.Participant;
import info.glennengstrand.util.Link;
import info.glennengstrand.dao.cassandra.InboundRepository;
import info.glennengstrand.dao.cassandra.NewsFeedItemKey;
import info.glennengstrand.dao.cassandra.OutboundRepository;
import info.glennengstrand.dao.mysql.Friend;
import info.glennengstrand.dao.mysql.FriendRepository;
import info.glennengstrand.dao.mysql.ParticipantRepository;
import info.glennengstrand.resources.ParticipantApi;
import io.swagger.configuration.RedisConfiguration.FriendRedisTemplate;
import io.swagger.configuration.RedisConfiguration.ParticipantRedisTemplate;

@RunWith(SpringRunner.class)
public class ParticipantServiceTest {

    @TestConfiguration
    static class ParticipantServiceImplTestContextConfiguration {
        @Bean
        public ParticipantApi participantService() {
            return new ParticipantService();
        }

    }
        @Autowired
        private ParticipantApi participantService;

        @MockBean
        private OutboundRepository outboundRepository;

        @MockBean
        private FriendRepository friendRepository;
        
        @MockBean
        private ParticipantRepository participantRepository;
        
        @MockBean
        private InboundRepository inboundRepository;

        @MockBean
	private FriendRedisTemplate friendRedisTemplate;

        @MockBean
        private ParticipantRedisTemplate participantRedisTemplate;
    
        @MockBean
        private RestHighLevelClient esClient;
    
        private static final Long fromParticipantId = 1L;
        private static final Long toParticipantId = 2L;
        private static final String TEST_NAME = "test participant";
        private static final String TEST_STORY = "test story";
        private static final String TEST_SUBJECT = "test subject";

        @Before
        public void setUp() throws Exception {
            List<Friend> friends = new ArrayList<>();
            Friend f = new Friend();
            f.setId(1l);
            f.setFromParticipantId(fromParticipantId);
            f.setToParticipantId(toParticipantId);
            friends.add(f);
            
	    Mockito.when(friendRepository.findByFromParticipantId(fromParticipantId)).thenReturn(friends);
          
	    Mockito.when(friendRepository.findByToParticipantId(fromParticipantId)).thenReturn(friends);
        Mockito.when(participantRedisTemplate.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(participantRedisTemplate.boundValueOps(Mockito.anyString())).thenReturn(new ParticipantRedisOperation());
        }
    
        @Test
        public void testGetParticipant() {
            ParticipantApi participantService = this.participantService;
            Mockito.when(participantRepository.findById(1L)).thenReturn(Optional.ofNullable(new info.glennengstrand.dao.mysql.Participant()));
            Participant t = participantService.getParticipant(1L);
            assertNotNull(t);
        }
        
        @Test
        public void testAddParticipant() {
            ParticipantApi participantService = this.participantService;
            Mockito.when(participantRepository.save(Mockito.any())).thenReturn(new info.glennengstrand.dao.mysql.Participant());
            Participant t = participantService.addParticipant(new Participant());
            assertNotNull(t);
        }

	@Test
	public void testGetOutbound() {
		List<Outbound> result = participantService.getOutbound(fromParticipantId);
		assertEquals(result.size(), 1);
		Outbound r = result.get(0);
		assertEquals(r.getStory(), TEST_STORY);
		assertEquals(r.getSubject(), TEST_SUBJECT);
	}

}
