using Microsoft.AspNetCore.Mvc;
using newsfeed.Interfaces;
using newsfeed.Models;

namespace newsfeed.DAOs;

public class ParticipantDao : IParticipantDao
{
    static readonly ILogger<ParticipantDao> logger = new LoggerFactory().CreateLogger<ParticipantDao>();

    public async Task<Participant> CreateParticipantAsync(Participant participant)
    {
        throw new NotImplementedException();
    }

    public async Task<Participant?> GetParticipantAsync(string id)
    {
        throw new NotImplementedException();
    }
}