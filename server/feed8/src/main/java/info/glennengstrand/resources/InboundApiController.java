package info.glennengstrand.resources;

import info.glennengstrand.api.Inbound;
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
@Api(value = "inbound")
public class InboundApiController {

    @Autowired
    private InboundApi service;

    private static final Logger log = LoggerFactory.getLogger(InboundApiController.class);


    @ApiOperation(value = "retrieve the inbound feed for an individual participant", nickname = "getInbound", notes = "fetch inbound feed by id", response = Inbound.class, responseContainer = "List", tags={ "inbound", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Inbound.class, responseContainer = "List") })
    @RequestMapping(value = "/inbound/{id}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    public List<Inbound> getInbound(@ApiParam(value = "uniquely identifies the participant",required=true) @PathVariable("id") Long id) {
        return service.getInbound(id);
    }
}
