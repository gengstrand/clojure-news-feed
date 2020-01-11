package info.glennengstrand.resources;

import info.glennengstrand.api.Friend;
import info.glennengstrand.api.Inbound;
import info.glennengstrand.api.Outbound;
import info.glennengstrand.api.Participant;
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
@Api(value = "participant")
public class ParticipantApiController {

    @Autowired
    private ParticipantApi service;

    private static final Logger log = LoggerFactory.getLogger(ParticipantApiController.class);


    @ApiOperation(value = "create a new friendship", nickname = "addFriend", notes = "friends are those participants who receive news", response = Friend.class, tags={ "friend", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Friend.class) })
    @RequestMapping(value = "/participant/{id}/friends",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    public Friend addFriend(@ApiParam(value = "uniquely identifies the participant",required=true) @PathVariable("id") Long id,@ApiParam(value = "friendship to be created" ,required=true )  @Valid @RequestBody Friend body) {
        return service.addFriend(id, body);
    }

    @ApiOperation(value = "create a participant news item", nickname = "addOutbound", notes = "socially broadcast participant news", response = Outbound.class, tags={ "outbound", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Outbound.class) })
    @RequestMapping(value = "/participant/{id}/outbound",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    public Outbound addOutbound(@ApiParam(value = "uniquely identifies the participant",required=true) @PathVariable("id") Long id,@ApiParam(value = "outbound news item" ,required=true )  @Valid @RequestBody Outbound body) {
        return service.addOutbound(id, body);
    }

    @ApiOperation(value = "create a new participant", nickname = "addParticipant", notes = "a participant is someone who can post news to friends", response = Participant.class, tags={ "participant", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Participant.class) })
    @RequestMapping(value = "/participant",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    public Participant addParticipant(@ApiParam(value = "participant to be created" ,required=true )  @Valid @RequestBody Participant body) {
        return service.addParticipant(body);
    }

    @ApiOperation(value = "retrieve the list of friends for an individual participant", nickname = "getFriend", notes = "fetch participant friends", response = Friend.class, responseContainer = "List", tags={ "friend", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Friend.class, responseContainer = "List") })
    @RequestMapping(value = "/participant/{id}/friends",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    public List<Friend> getFriend(@ApiParam(value = "uniquely identifies the participant",required=true) @PathVariable("id") Long id) {
        return service.getFriend(id);
    }

    @ApiOperation(value = "retrieve the inbound feed for an individual participant", nickname = "getInbound", notes = "fetch inbound feed by id", response = Inbound.class, responseContainer = "List", tags={ "inbound", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Inbound.class, responseContainer = "List") })
    @RequestMapping(value = "/participant/{id}/inbound",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    public List<Inbound> getInbound(@ApiParam(value = "uniquely identifies the participant",required=true) @PathVariable("id") Long id) {
        return service.getInbound(id);
    }

    @ApiOperation(value = "retrieve the news posted by an individual participant", nickname = "getOutbound", notes = "fetch a participant news", response = Outbound.class, responseContainer = "List", tags={ "outbound", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Outbound.class, responseContainer = "List") })
    @RequestMapping(value = "/participant/{id}/outbound",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    public List<Outbound> getOutbound(@ApiParam(value = "uniquely identifies the participant",required=true) @PathVariable("id") Long id) {
        return service.getOutbound(id);
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
