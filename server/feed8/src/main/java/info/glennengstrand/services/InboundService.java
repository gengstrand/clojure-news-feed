package info.glennengstrand.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import info.glennengstrand.api.Inbound;
import info.glennengstrand.dao.cassandra.InboundRepository;
import info.glennengstrand.resources.InboundApi;

import com.datastax.driver.core.utils.UUIDs;

@Service
public class InboundService implements InboundApi {

	@Autowired
	private InboundRepository repository;

	@Override
	public List<Inbound> getInbound(Integer id) {
		return repository.findByNewsFeedItemKey_ParticipantId(id).map(i -> {
			return new Inbound()
					.from(i.getFromParticipantId())
					.to(i.getParticipantId())
					.occurred(convert(UUIDs.unixTimestamp(i.getOccured())))
					.subject(i.getSubject())
					.story(i.getStory());
		}).collect(Collectors.toList());
	}

	@Override
	public Inbound addInbound(Inbound body) {
		info.glennengstrand.dao.cassandra.Inbound i = new info.glennengstrand.dao.cassandra.Inbound();
		i.setParticipantId(body.getTo());
		i.setFromParticipantId(body.getFrom());
		i.setSubject(body.getSubject());
		i.setStory(body.getStory());
		repository.save(i);
		return body;
	}

}
