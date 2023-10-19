namespace newsfeed.Models;

public class Inbound : Outbound {
    public Inbound(string from, string to, DateOnly occurred, string subject, string story) : base(from, occurred, subject, story) {
        To = to;
    }

    public string To { get; }

}