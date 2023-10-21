using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using newsfeed.Models;
using newsfeed.Interfaces;

namespace newsfeed.Controllers;

[ApiController]
[Route("[controller]")]
public class ParticipantController : ControllerBase
{
    private readonly ILogger<ParticipantController> _logger;
    private readonly IParticipantService _service;

    public ParticipantController(ILogger<ParticipantController> logger, IParticipantService service)
    {
        _logger = logger;
        _service = service;
    }

    [HttpGet("{id}")]
    public async Task<Participant> Get(string id)
    {
	    var rv = await _service.GetParticipant(id);
	    if (rv == null) {
	       Response.StatusCode = 404;
	    }
	    return rv;
    }

    [HttpPost]
    public async Task<Participant> Create([FromBody] Participant participant)
    {
	    return await _service.CreateParticipant(participant);
    }

    [HttpGet("{id}/friends")]
    public async Task<IEnumerable<Friend>> GetFriends(string id)
    {
	    return await _service.GetFriends(id);
    }

    [HttpPost("{id}/friends")]
    public async Task<Friend> CreateFriend(string id, [FromBody] Friend friend)
    {
	    return await _service.CreateFriend(id, friend);
    }

    [HttpGet("{id}/outbound")]
    public async Task<IEnumerable<Outbound>> GetOutbound(string id)
    {
	    return await _service.GetOutbound(id);
    }

    [HttpPost("{id}/outbound")]
    public async Task<Outbound> CreateOutbound(string id, [FromBody] Outbound outbound) {
	    return await _service.CreateOutbound(id, outbound);
    }

    [HttpGet("{id}/inbound")]
    public async Task<IEnumerable<Inbound>> GetInbound(string id)
    {
	    return await _service.GetInbound(id);
    }

}
