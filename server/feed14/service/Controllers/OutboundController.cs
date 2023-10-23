using Microsoft.AspNetCore.Mvc;
using newsfeed.Interfaces;
using System.Text.Json;

namespace newsfeed.Controllers;

[ApiController]
[Route("[controller]")]
public class OutboundController : ControllerBase
{
    private readonly ILogger<OutboundController> _logger;
    private readonly IOutboundService _service;

    public OutboundController(ILogger<OutboundController> logger, IOutboundService service)
    {
        _logger = logger;
        _service = service;
    }

    [HttpGet]
    public async Task<IEnumerable<string>> Search([FromQuery] string keywords) {
        var r = await _service.Search(keywords);
	List<string> rv = new();
	foreach (var i in r) {
	    rv.Add($"/participant/{i}");
	}
	return rv;
    }

}