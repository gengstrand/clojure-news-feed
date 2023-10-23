using Microsoft.AspNetCore.Mvc;
using System.Text.Json;
using newsfeed.Interfaces;
using newsfeed.Models;

namespace newsfeed.Services;

public class ParticipantService : IParticipantService
{
    static readonly ILogger<ParticipantService> logger = new LoggerFactory().CreateLogger<ParticipantService>();
    static readonly JsonSerializerOptions jo = new() {
       PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
    };

    private readonly IParticipantDao _participantDao;
    private readonly IFriendDao _friendDao;
    private readonly IOutboundDao _outboundDao;
    private readonly IInboundDao _inboundDao;
    private readonly ICacheDao _cacheDao;
    private readonly ISearchDao _searchDao;

    public ParticipantService(IParticipantDao participantDao, IFriendDao friendDao, IOutboundDao outboundDao, IInboundDao inboundDao, ICacheDao cacheDao, ISearchDao searchDao) {
        _participantDao = participantDao;
        _friendDao = friendDao;
        _outboundDao = outboundDao;
        _inboundDao = inboundDao;
        _cacheDao = cacheDao;
        _searchDao = searchDao;
    }

    public async Task<Friend> CreateFriend(string id, Friend friend)
    {
        return await _friendDao.CreateFriendAsync(id, friend);
    }

    public async Task<Outbound> CreateOutbound(string id, Outbound outbound)
    {
        var r =  await _outboundDao.CreateOutboundAsync(id, outbound);
        var friends = await _friendDao.GetFriendsAsync(id);
        if (friends != null) {
            var d = new DateOnly().ToString();
            foreach(Friend f in friends) {
                var t = f.From == id ? f.To : f.From;
                var i = new Inbound(id, t, d, outbound.Subject, outbound.Story);
                _inboundDao.CreateInboundAsync(id, i);
            }
        }
        _searchDao.IndexAsync(Guid.NewGuid().ToString(), outbound.Subject, outbound.Story);
        return r;
    }    

    public async Task<Participant> CreateParticipant(Participant participant)
    {
        return await _participantDao.CreateParticipantAsync(participant);
    }

    public async Task<IEnumerable<Friend>> GetFriends(string id)
    {
        string k = "Friend::" + id;
        string? v = await _cacheDao.GetValueAsync(k);
        if (v == null) {
            var rv = await _friendDao.GetFriendsAsync(id);
            _cacheDao.SetValueAsync(k, JsonSerializer.Serialize(rv, jo));
            return rv;
        } else {
            var rv = JsonSerializer.Deserialize<IEnumerable<Friend>>(v, jo);
            return rv ?? new List<Friend>();
        }
    }

    public async Task<IEnumerable<Inbound>> GetInbound(string id)
    {
        return await _inboundDao.GetInboundAsync(id);
    }

    public async Task<IEnumerable<Outbound>> GetOutbound(string id)
    {
        return await _outboundDao.GetOutboundAsync(id);
    }

    public async Task<Participant?> GetParticipant(string id)
    {
        string k = "Participant::" + id;
        string? v = await _cacheDao.GetValueAsync(k);
        if (v == null) 
        {
            var rv = await _participantDao.GetParticipantAsync(id);
            if (rv != null) {
                _cacheDao.SetValueAsync(k, JsonSerializer.Serialize(rv, jo));
            }
            return rv;
        } else {
            var rv = JsonSerializer.Deserialize<Participant>(v, jo);
            return rv;
        
        }
    }
}
