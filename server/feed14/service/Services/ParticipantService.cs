using Microsoft.AspNetCore.Mvc;
using newsfeed.Interfaces;
using newsfeed.Models;

namespace newsfeed.Services;

public class ParticipantService : IParticipantService
{
    static readonly ILogger<ParticipantService> logger = new LoggerFactory().CreateLogger<ParticipantService>();

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
        if (r != null) {
            var friends = await _friendDao.GetFriendsAsync(id);
            if (friends != null) {
                var d = new DateOnly();
                foreach(Friend f in friends) {
                    var t = f.From == id ? f.To : f.From;
                    var i = new Inbound(id, t, d, outbound.Subject, outbound.Story);
                    _inboundDao.CreateInboundAsync(id, i);
                }
            }
        }
        return r;
    }    

    public async Task<Participant> CreateParticipant(Participant participant)
    {
        return await _participantDao.CreateParticipantAsync(participant);
    }

    public async Task<IEnumerable<Friend>> GetFriends(string id)
    {
        return await _friendDao.GetFriendsAsync(id);
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
        return await _participantDao.GetParticipantAsync(id);
    }
}
