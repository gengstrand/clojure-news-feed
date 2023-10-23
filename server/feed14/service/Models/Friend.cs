namespace newsfeed.Models;

public class Friend {

    public Friend(string? id, string from, string to) {
        Id = id ?? "";
        From = from;
        To = to;
    }
    
    public string Id { get; }

    public string From { get; }

    public string To { get; }

}