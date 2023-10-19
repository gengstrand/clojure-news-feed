namespace tests;

using Xunit;
using Microsoft.Extensions.Logging;
using newsfeed.Controllers;
using newsfeed.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Http.HttpResults;

public class OutboundControllerUnitTests
{
    static readonly ILogger<OutboundController> logger = new LoggerFactory().CreateLogger<OutboundController>();
    OutboundController controller = new OutboundController(logger);

    [Fact]
    public void TestSearch() {
        ActionResult<IEnumerable<string>> result = controller.Search("test");
        Assert.IsType<NotFoundResult>(result.Result);
    }
}