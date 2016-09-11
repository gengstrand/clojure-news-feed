package info.glennengstrand.core;

import java.util.List;

import com.google.inject.Inject;

import info.glennengstrand.api.Inbound;
import info.glennengstrand.db.InboundDAO;
import info.glennengstrand.resources.InboundApi.InboundApiService;

public class InboundApiServiceImpl implements InboundApiService {

	private final InboundDAO dao;
	
	@Override
	public List<Inbound> getInbound(Long id) {
		return dao.fetch(id);
	}
	
	@Inject
	public InboundApiServiceImpl(InboundDAO dao) {
		this.dao = dao;
	}

}
