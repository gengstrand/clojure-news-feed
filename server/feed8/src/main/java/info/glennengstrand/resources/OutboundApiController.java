package info.glennengstrand.resources;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@Api(value = "outbound")
public class OutboundApiController {

    @Autowired
    private OutboundApi service;

    private static final Logger log = LoggerFactory.getLogger(OutboundApiController.class);


    @ApiOperation(value = "search outbound feed items for terms", nickname = "searchOutbound", notes = "keyword search of participant news", response = String.class, responseContainer = "List", tags={ "outbound", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = String.class, responseContainer = "List") })
    @RequestMapping(value = "/outbound",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    public List<String> searchOutbound(@NotNull @ApiParam(value = "keywords to search for", required = true) @Valid @RequestParam(value = "keywords", required = true) String keywords) {
        return service.searchOutbound(keywords);
    }
}
