namespace newsfeed.Models;

public class Participant
{
    public Participant(string id, string name) {
        Id = id;
        Name = name;
        Link = $"/participant/{id}";
    }
    
    public string Id { get; }

    public string Name { get; }

    public string Link { get; }
}
