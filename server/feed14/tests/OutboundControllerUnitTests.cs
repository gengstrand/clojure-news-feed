namespace tests;

using Xunit;
using Moq;
using System.Text.Json;
using Microsoft.Extensions.Logging;
using newsfeed.Controllers;
using newsfeed.Models;
using newsfeed.Services;
using newsfeed.Interfaces;

using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Http.HttpResults;

public class OutboundControllerUnitTests
{
    static readonly ILogger<OutboundController> logger = new LoggerFactory().CreateLogger<OutboundController>();
    static readonly Mock<ISearchDao> searchDaoMock = new();
    static readonly IOutboundService service = new OutboundService(searchDaoMock.Object);
    static readonly OutboundController controller = new(logger, service);

    static OutboundControllerUnitTests() {
        searchDaoMock.Setup(dao => dao.IndexAsync("1", "1", "test")).ReturnsAsync(true);
        searchDaoMock.Setup(dao => dao.SearchAsync("test")).ReturnsAsync(new List<string>{"1"});
    }

    [Fact]
    public async void TestSearch() {
        IEnumerable<string> result = await controller.Search("test");
        Assert.NotNull(result);
        Assert.Single(result);
        Assert.Equal("/participant/1", result.First());
    }
}