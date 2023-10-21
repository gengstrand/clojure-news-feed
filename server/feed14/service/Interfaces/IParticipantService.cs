using Microsoft.AspNetCore.Mvc;
using newsfeed.Models;

namespace newsfeed.Interfaces;

public interface IParticipantService {
    Task<Participant?> GetParticipant(string id);
    Task<Participant> CreateParticipant(Participant participant);
    Task<IEnumerable<Friend>> GetFriends(string id);
    Task<Friend> CreateFriend(string id, [FromBody] Friend friend);
    Task<IEnumerable<Outbound>> GetOutbound(string id);
    Task<Outbound> CreateOutbound(string id, [FromBody] Outbound outbound);
    Task<IEnumerable<Inbound>> GetInbound(string id);
}