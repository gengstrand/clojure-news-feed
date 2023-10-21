namespace tests;

using Xunit;
using Moq;
using Microsoft.Extensions.Logging;
using System.Text.Json;
using newsfeed.Controllers;
using newsfeed.Models;
using newsfeed.Services;
using newsfeed.Interfaces;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Http.HttpResults;
using System.Threading.Tasks;

public class ParticipantControllerUnitTests
{
    static readonly ILogger<ParticipantController> logger = new LoggerFactory().CreateLogger<ParticipantController>();
    static readonly Participant participant = new Participant("1", "Glenn");
    static readonly Friend friend = new("1", "1", "2");
    static readonly Outbound outbound = new("1", new DateOnly(), "test subject", "test story");
    static readonly Inbound inbound = new("1", "2", new DateOnly(), "test subject", "test story");
    static readonly string ps = JsonSerializer.Serialize(participant);

    private ParticipantController GetController() {    
        Mock<IParticipantDao> participantDaoMock = new();
        Mock<IFriendDao> friendDaoMock = new();
        Mock<IOutboundDao> outboundDaoMock = new();
        Mock<IInboundDao> inboundDaoMock = new();
        Mock<ICacheDao> cacheDaoMock = new();
        Mock<ISearchDao> searchDaoMock = new();
        participantDaoMock.Setup(p => p.GetParticipantAsync(It.IsAny<string>())).ReturnsAsync(participant);
        participantDaoMock.Setup(p => p.CreateParticipantAsync(It.IsAny<Participant>())).ReturnsAsync(participant);
        friendDaoMock.Setup(f => f.GetFriendsAsync(It.IsAny<string>())).ReturnsAsync(new List<Friend>() { friend });
        friendDaoMock.Setup(f => f.CreateFriendAsync(It.IsAny<string>(), It.IsAny<Friend>())).ReturnsAsync(friend);
        outboundDaoMock.Setup(o => o.GetOutboundAsync(It.IsAny<string>())).ReturnsAsync(new List<Outbound>() { outbound });
        outboundDaoMock.Setup(o => o.CreateOutboundAsync(It.IsAny<string>(), It.IsAny<Outbound>())).ReturnsAsync(outbound);
        inboundDaoMock.Setup(i => i.GetInboundAsync(It.IsAny<string>())).ReturnsAsync(new List<Inbound>() { inbound });
        inboundDaoMock.Setup(i => i.CreateInboundAsync(It.IsAny<string>(), It.IsAny<Inbound>())).ReturnsAsync(inbound);
        cacheDaoMock.Setup(c => c.GetValueAsync(It.IsAny<string>() )).ReturnsAsync(ps);
        cacheDaoMock.Setup(c => c.SetValueAsync(It.IsAny<string>(), It.IsAny<string>())).ReturnsAsync(true);
        searchDaoMock.Setup(s => s.IndexAsync(It.IsAny<string>(), It.IsAny<string>(), It.IsAny<string>())).ReturnsAsync(true);
        searchDaoMock.Setup(s => s.SearchAsync(It.IsAny<string>())).ReturnsAsync(new List<string>() { "1" });
	IParticipantService participantService = new ParticipantService(participantDaoMock.Object, friendDaoMock.Object, outboundDaoMock.Object, inboundDaoMock.Object, cacheDaoMock.Object, searchDaoMock.Object);
	return new ParticipantController(logger, participantService);
    }

    [Fact]
    public async void TestGetParticipant()
    {
	var controller = GetController();
        Participant? result = await controller.Get("1");
        Assert.NotNull(result);
        Assert.Equal(participant.Id, result.Id);
        Assert.Equal(participant.Name, result.Name);
        Assert.Equal(participant.Link, result.Link);
    }

    [Fact]
    public async void TestCreatParticipant()
    {
	var controller = GetController();
        Participant result = await controller.Create(participant);
        Assert.Equal(participant.Id, result.Id);
        Assert.Equal(participant.Name, result.Name);
        Assert.Equal(participant.Link, result.Link);
    }

    [Fact]
    public async void TestGetFriends() {
	var controller = GetController();
        IEnumerable<Friend> result = await controller.GetFriends("1");
        Assert.Single(result);
        Assert.Equal(friend.From, result.First().From);
        Assert.Equal(friend.To, result.First().To);
    }

    [Fact]
    public async void TestCreateFriend() {
	var controller = GetController();
        Friend result = await controller.CreateFriend("1", friend);
        Assert.Equal(friend.From, result.From);
        Assert.Equal(friend.To, result.To);
    }

    [Fact]
    public async void TestGetOutbound() {
	var controller = GetController();
        IEnumerable<Outbound> result = await controller.GetOutbound("1");
        Assert.Single(result);
        Assert.Equal(outbound.Subject, result.First().Subject);
        Assert.Equal(outbound.Story, result.First().Story);
    }

    [Fact]
    public async void TestCreateOutbound() {
	var controller = GetController();
        Outbound result = await controller.CreateOutbound("1", outbound);
        Assert.Equal(outbound.Subject, result.Subject);
        Assert.Equal(outbound.Story, result.Story);
    }

    [Fact]
    public async void TestGetInbound() {
	var controller = GetController();
        IEnumerable<Inbound> result = await controller.GetInbound("1"); 
        Assert.Single(result);
        Assert.Equal(inbound.Subject, result.First().Subject);
        Assert.Equal(inbound.Story, result.First().Story);
    }

}