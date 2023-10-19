using Microsoft.AspNetCore.Mvc;
using newsfeed.Models;
namespace newsfeed.Controllers;

[ApiController]
[Route("[controller]")]
public class ParticipantController : ControllerBase
{
    private readonly ILogger<ParticipantController> _logger;

    public ParticipantController(ILogger<ParticipantController> logger)
    {
        _logger = logger;
    }

    [HttpGet("{id}")]
    public ActionResult<Participant> Get(string id)
    {
	    return NotFound();
    }

    [HttpPost]
    public ActionResult<Participant> Create([FromBody] Participant participant)
    {
	    return NotFound();
    }

    [HttpGet("{id}/friends")]
    public ActionResult<IEnumerable<Friend>> GetFriends(string id)
    {
	    return NotFound();
    }

    [HttpPost("{id}/friends")]
    public ActionResult<Friend> CreateFriend(string id, [FromBody] Friend friend) {
	    return NotFound();
    }

    [HttpGet("{id}/outbound")]
    public ActionResult<IEnumerable<Outbound>> GetOutbound(string id)
    {
	    return NotFound();
    }

    [HttpPost("{id}/outbound")]
    public ActionResult<Outbound> CreateOutbound(string id, [FromBody] Outbound outbound) {
	    return NotFound();
    }

    [HttpGet("{id}/inbound")]
    public ActionResult<IEnumerable<Inbound>> GetInbound(string id)
    {
	    return NotFound();
    }

}
