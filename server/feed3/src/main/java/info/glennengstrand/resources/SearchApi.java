package info.glennengstrand.resources;

import com.google.inject.Inject;  
import javax.ws.rs.GET;  
import javax.ws.rs.QueryParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import info.glennengstrand.resources.OutboundApi.OutboundApiService;

@Path("/outbound")
public class SearchApi {
	   private final OutboundApiService outboundService;

	   @Inject
	   public SearchApi(OutboundApiService outboundService) {
	      this.outboundService = outboundService;
	   }
	   
	   @GET
	   @Produces("application/json")
	  /**
	   * search outbound feed items for terms
	   * keyword search of participant news
	   * @param keywords keywords to search for (required)
	   * @return List<String>
	   */
	   public List<String> searchOutbound(@QueryParam("keywords") String keywords) {
	      return outboundService.searchOutbound(keywords);
	   }

}
