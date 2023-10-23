using System.Threading.Tasks;
using System.Text.RegularExpressions;
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
    private static readonly Regex linkPattern = new("/participant/([0-9]+)");
    private string extractId(string link) {
    	    MatchCollection matches = linkPattern.Matches(link);
	    var rv = link;
	    foreach(Match m in matches) {
	       rv = m.Groups[1].Value;
	    }
	    return rv;
    }

    private string generateLink(string id) {
    	    return $"/participant/{id}";
    }

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
	    List<Friend> rv = new();
	    var r = await _service.GetFriends(id);
	    foreach(var i in r) {
	        rv.Add(new Friend(i.Id, generateLink(i.From), generateLink(i.To)));
	    }
	    return rv;
    }

    [HttpPost("{id}/friends")]
    public async Task<Friend> CreateFriend(string id, [FromBody] Friend friend)
    {
	    Friend f = new(id, extractId(friend.From), extractId(friend.To));
	    return await _service.CreateFriend(id, f);
    }

    [HttpGet("{id}/outbound")]
    public async Task<IEnumerable<Outbound>> GetOutbound(string id)
    {
	    List<Outbound> rv = new();
	    var r = await _service.GetOutbound(id);
	    foreach (var i in r) {
	        rv.Add(new Outbound(generateLink(i.From), i.Occurred, i.Subject, i.Story));
	    }
	    return rv;
    }

    [HttpPost("{id}/outbound")]
    public async Task<Outbound> CreateOutbound(string id, [FromBody] Outbound outbound) {
    	   Outbound o = new(extractId(outbound.From), new DateOnly().ToString(), outbound.Subject, outbound.Story);
	   return await _service.CreateOutbound(id, o);
    }

    [HttpGet("{id}/inbound")]
    public async Task<IEnumerable<Inbound>> GetInbound(string id)
    {
	    List<Inbound> rv = new();
	    var r = await _service.GetInbound(id);
	    foreach (var i in r) {
	        rv.Add(new Inbound(generateLink(i.From), generateLink(i.To), i.Occurred, i.Subject, i.Story));
	    }
	    return rv;
    }

}
