package info.glennengstrand.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import info.glennengstrand.api.Participant;
import info.glennengstrand.resources.NotFoundException;
import info.glennengstrand.resources.ParticipantApi;
import io.swagger.configuration.RedisConfiguration.ParticipantRedisTemplate;
import info.glennengstrand.dao.mysql.ParticipantRepository;

@Service
public class ParticipantService implements ParticipantApi {

	@Autowired
	private ParticipantRepository repository;
	
	@Autowired
	private ParticipantRedisTemplate template;
	
	@Override
	public Participant addParticipant(Participant body) {
		info.glennengstrand.dao.mysql.Participant p = new info.glennengstrand.dao.mysql.Participant();
		p.setMoniker(body.getName());
		info.glennengstrand.dao.mysql.Participant retVal = repository.save(p);
		return body.id(retVal.getId());
	}

	@Override
	public Participant getParticipant(Long id) {
		String key = "Participant::".concat(id.toString());
		if (template.hasKey(key)) {
			return template.boundValueOps(key).get();
		} else {
			Optional<info.glennengstrand.dao.mysql.Participant> r = repository.findById(id);
			if (r.isPresent()) {
				info.glennengstrand.dao.mysql.Participant p = r.get();
				Participant retVal = new Participant().id(p.getId()).name(p.getMoniker());
				template.boundValueOps(key).set(retVal);
				return retVal;
			} else {
				throw new NotFoundException(404, String.format("participant {} not found", id));
			}
		}
	}

}
