package info.glennengstrand.resources;

import info.glennengstrand.api.Friend;
import info.glennengstrand.api.Inbound;
import info.glennengstrand.api.Outbound;
import info.glennengstrand.api.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ParticipantApiController {

    @Autowired
    private ParticipantApi service;

    private static final Logger log = LoggerFactory.getLogger(ParticipantApiController.class);


    @RequestMapping(value = "/participant/{id}/friends",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    public Friend addFriend(@PathVariable("id") Long id, @RequestBody Friend body) {
        return service.addFriend(id, body);
    }

    @RequestMapping(value = "/participant/{id}/outbound",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    public Outbound addOutbound(@PathVariable("id") Long id, @RequestBody Outbound body) {
        return service.addOutbound(id, body);
    }

    @RequestMapping(value = "/participant",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    public Participant addParticipant(@RequestBody Participant body) {
        return service.addParticipant(body);
    }

    @RequestMapping(value = "/participant/{id}/friends",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    public List<Friend> getFriend(@PathVariable("id") Long id) {
        return service.getFriend(id);
    }

    @RequestMapping(value = "/participant/{id}/inbound",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    public List<Inbound> getInbound(@PathVariable("id") Long id) {
        return service.getInbound(id);
    }

    @RequestMapping(value = "/participant/{id}/outbound",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    public List<Outbound> getOutbound(@PathVariable("id") Long id) {
        return service.getOutbound(id);
    }

    @RequestMapping(value = "/participant/{id}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    public Participant getParticipant(@PathVariable("id") Long id) {
        return service.getParticipant(id);
    }
}
