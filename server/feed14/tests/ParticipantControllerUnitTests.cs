namespace tests;

using Xunit;
using Microsoft.Extensions.Logging;
using newsfeed.Controllers;
using newsfeed.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Http.HttpResults;

public class ParticipantControllerUnitTests
{
    static readonly ILogger<ParticipantController> logger = new LoggerFactory().CreateLogger<ParticipantController>();
    ParticipantController controller = new ParticipantController(logger);
    
    [Fact]
    public void TestGetParticipant()
    {
        ActionResult<Participant> result = controller.Get("1");
        Assert.IsType<NotFoundResult>(result.Result);
    }

    [Fact]
    public void TestCreatParticipant()
    {
        Participant participant = new Participant("1", "Glenn");
        ActionResult<Participant> result = controller.Create(participant);
        Assert.IsType<NotFoundResult>(result.Result);
    }

    [Fact]
    public void TestGetFriends() {
        ActionResult<IEnumerable<Friend>> result = controller.GetFriends("1");
        Assert.IsType<NotFoundResult>(result.Result);
    }

    [Fact]
    public void TestCreateFriend() {
        Friend friend = new Friend("1", "1", "2");
        ActionResult<Friend> result = controller.CreateFriend("1", friend);
        Assert.IsType<NotFoundResult>(result.Result);
    }

    [Fact]
    public void TestGetOutbound() {
        ActionResult<IEnumerable<Outbound>> result = controller.GetOutbound("1");
        Assert.IsType<NotFoundResult>(result.Result);
    }

    [Fact]
    public void TestCreateOutbound() {
        Outbound outbound = new Outbound("1", new DateOnly(), "test subject", "test story");
        ActionResult<Outbound> result = controller.CreateOutbound("1", outbound);
        Assert.IsType<NotFoundResult>(result.Result);
    }

    [Fact]
    public void TestGetInbound() {
        ActionResult<IEnumerable<Inbound>> result = controller.GetInbound("1"); 
        Assert.IsType<NotFoundResult>(result.Result);
    }

}