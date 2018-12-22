package info.glennengstrand.resources;

import info.glennengstrand.api.Friend;
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
@Api(value = "friends")
public class FriendsApiController {

    @Autowired
    private FriendsApi service;

    private static final Logger log = LoggerFactory.getLogger(FriendsApiController.class);


    @ApiOperation(value = "create a new friendship", nickname = "addFriend", notes = "friends are those participants who receive news", response = Friend.class, tags={ "friend", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Friend.class) })
    @RequestMapping(value = "/friends/new",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    public Friend addFriend(@ApiParam(value = "friendship to be created" ,required=true )  @Valid @RequestBody Friend body) {
        return service.addFriend(body);
    }

    @ApiOperation(value = "retrieve the list of friends for an individual participant", nickname = "getFriend", notes = "fetch participant friends", response = Friend.class, responseContainer = "List", tags={ "friend", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Friend.class, responseContainer = "List") })
    @RequestMapping(value = "/friends/{id}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    public List<Friend> getFriend(@ApiParam(value = "uniquely identifies the participant",required=true) @PathVariable("id") Long id) {
        return service.getFriend(id);
    }
}
