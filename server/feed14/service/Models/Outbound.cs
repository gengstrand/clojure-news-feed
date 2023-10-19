namespace newsfeed.Models;

public class Outbound
{
    public Outbound(string from, DateOnly occurred, string subject, string story) {
        From = from;
        Occurred = occurred.ToString();
        Subject = subject;
        Story = story;
    }
    
    public string From { get; }

    public string Occurred { get; }

    public string Subject { get; }

    public string Story { get; }

}
