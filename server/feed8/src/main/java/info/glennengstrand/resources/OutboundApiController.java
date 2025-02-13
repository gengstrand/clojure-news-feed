package info.glennengstrand.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class OutboundApiController {

    @Autowired
    private OutboundApi service;

    private static final Logger log = LoggerFactory.getLogger(OutboundApiController.class);

    @RequestMapping(value = "/outbound",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    public List<String> searchOutbound(@RequestParam(value = "keywords", required = true) String keywords) {
        return service.searchOutbound(keywords);
    }
}
