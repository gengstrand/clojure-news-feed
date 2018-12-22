package info.glennengstrand.resources;

import info.glennengstrand.api.Outbound;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;

import java.util.List;

import javax.validation.Valid;

@RestController
@Api(value = "outbound")
public class OutboundApiController {

    @Autowired
    private OutboundApi service;

    private static final Logger log = LoggerFactory.getLogger(OutboundApiController.class);


    @ApiOperation(value = "create a participant news item", nickname = "addOutbound", notes = "socially broadcast participant news", response = Outbound.class, tags={ "outbound", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Outbound.class) })
    @RequestMapping(value = "/outbound/new",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    public Outbound addOutbound(@ApiParam(value = "outbound news item" ,required=true )  @Valid @RequestBody Outbound body) {
        return service.addOutbound(body);
    }

    @ApiOperation(value = "retrieve the news posted by an individual participant", nickname = "getOutbound", notes = "fetch a participant news", response = Outbound.class, responseContainer = "List", tags={ "outbound", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Outbound.class, responseContainer = "List") })
    @RequestMapping(value = "/outbound/{id}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    public List<Outbound> getOutbound(@ApiParam(value = "uniquely identifies the participant",required=true) @PathVariable("id") Long id) {
        return service.getOutbound(id);
    }

    @ApiOperation(value = "create a participant news item", nickname = "searchOutbound", notes = "keyword search of participant news", response = Long.class, responseContainer = "List", tags={ "outbound", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Long.class, responseContainer = "List") })
    @RequestMapping(value = "/outbound/search",
        produces = { "application/json" }, 
        method = RequestMethod.POST)
    public List<Long> searchOutbound(@NotNull @ApiParam(value = "keywords to search for", required = true) @Valid @RequestParam(value = "keywords", required = true) String keywords) {
        return service.searchOutbound(keywords);
    }
}
