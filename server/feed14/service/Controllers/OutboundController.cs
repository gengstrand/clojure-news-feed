using Microsoft.AspNetCore.Mvc;

namespace newsfeed.Controllers;

[ApiController]
[Route("[controller]")]
public class OutboundController : ControllerBase
{
    private readonly ILogger<OutboundController> _logger;

    public OutboundController(ILogger<OutboundController> logger)
    {
        _logger = logger;
    }

    [HttpGet]
    public ActionResult<IEnumerable<string>> Search([FromQuery] string keywords) {
        return NotFound();
    }

}