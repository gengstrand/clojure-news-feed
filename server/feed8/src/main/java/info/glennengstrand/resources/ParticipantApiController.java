package info.glennengstrand.resources;

import info.glennengstrand.api.Participant;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;

@RestController
@Api(value = "participant")
public class ParticipantApiController {

    @Autowired
    private ParticipantApi service;

    private static final Logger log = LoggerFactory.getLogger(ParticipantApiController.class);


    @ApiOperation(value = "create a new participant", nickname = "addParticipant", notes = "a participant is someone who can post news to friends", response = Participant.class, tags={ "participant", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Participant.class) })
    @RequestMapping(value = "/participant/new",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    public Participant addParticipant(@ApiParam(value = "participant to be created" ,required=true )  @Valid @RequestBody Participant body) {
        return service.addParticipant(body);
    }

    @ApiOperation(value = "retrieve an individual participant", nickname = "getParticipant", notes = "fetch a participant by id", response = Participant.class, tags={ "participant", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Participant.class) })
    @RequestMapping(value = "/participant/{id}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    public Participant getParticipant(@ApiParam(value = "uniquely identifies the participant",required=true) @PathVariable("id") Long id) {
        return service.getParticipant(id);
    }
}
