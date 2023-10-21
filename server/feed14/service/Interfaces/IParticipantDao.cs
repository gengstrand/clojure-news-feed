using Microsoft.AspNetCore.Mvc;
using newsfeed.Models;

namespace newsfeed.Interfaces;

public interface IParticipantDao {
    Task<Participant?> GetParticipantAsync(string id);
    Task<Participant> CreateParticipantAsync(Participant participant);
}