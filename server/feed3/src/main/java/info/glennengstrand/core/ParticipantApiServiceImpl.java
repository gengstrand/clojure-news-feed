package info.glennengstrand.core;

import com.google.inject.Inject;

import info.glennengstrand.api.Participant;
import info.glennengstrand.db.ParticipantDAO;
import info.glennengstrand.resources.ParticipantApi.ParticipantApiService;

public class ParticipantApiServiceImpl implements ParticipantApiService {

	private final ParticipantDAO dao;
	
	@Inject
	public ParticipantApiServiceImpl(ParticipantDAO dao) {
		this.dao = dao;
	}
	
	@Override
	public Participant addParticipant(Participant body) {
		return getParticipant(dao.upsertParticipant(body.getName()));
	}

	@Override
	public Participant getParticipant(Long id) {
		return dao.fetchParticipant(id);
	}

}
