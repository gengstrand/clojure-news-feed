package info.glennengstrand.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import info.glennengstrand.api.Inbound;
import info.glennengstrand.dao.InboundRepository;
import info.glennengstrand.resources.InboundApi;

@Service
public class InboundService implements InboundApi {

	@Autowired
	private InboundRepository repository;

	@Override
	public List<Inbound> getInbound(Long id) {
		// TODO: figure out what to do with occurred
		return repository.findByParticipantId(id).stream().map(i -> {
			return new Inbound()
					.from(i.getFromParticipantId())
					.to(i.getParticipantId())
					.subject(i.getSubject())
					.story(i.getStory());
		}).collect(Collectors.toList());
	}

	@Override
	public Inbound addInbound(Inbound body) {
		info.glennengstrand.dao.Inbound i = new info.glennengstrand.dao.Inbound();
		i.setParticipantId(body.getTo());
		i.setFromParticipantId(body.getFrom());
		i.setSubject(body.getSubject());
		i.setStory(body.getStory());
		repository.save(i);
		return body;
	}

}
