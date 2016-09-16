package info.glennengstrand.core;

import java.util.List;

import com.google.inject.Inject;

import info.glennengstrand.api.Inbound;
import info.glennengstrand.db.InboundDAO;
import info.glennengstrand.resources.InboundApi.InboundApiService;

public class InboundApiServiceImpl implements InboundApiService {

	private static final String ENTITY = "Inbound";
	private final InboundDAO dao;
	private final MessageLogger<Long> logger;
	
	@Override
	public List<Inbound> getInbound(Long id) {
		long before = System.currentTimeMillis();
		List<Inbound> retVal = dao.fetch(id);
		logger.log(ENTITY, MessageLogger.LogOperation.GET, System.currentTimeMillis()- before);
		return retVal;
	}
	
	@Inject
	public InboundApiServiceImpl(InboundDAO dao, MessageLogger<Long> logger) {
		this.dao = dao;
		this.logger = logger;
	}

}
