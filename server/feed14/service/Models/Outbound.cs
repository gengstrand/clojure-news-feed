namespace newsfeed.Models;

public class Outbound
{
    public Outbound(string from, string? occurred, string subject, string story) {
        From = from;
        Occurred = occurred ?? new DateOnly().ToString();
        Subject = subject;
        Story = story;
    }
    
    public string From { get; }

    public string Occurred { get; }

    public string Subject { get; }

    public string Story { get; }

}
