using Microsoft.AspNetCore.Mvc;
using newsfeed.Interfaces;
using System.Text.Json;
using newsfeed.Models;

namespace newsfeed.DAOs;

public class SearchDao : ISearchDao
{
    class SearchDocument {
        public string Id { get; }
        public long Sender { get; }
        public string Story { get; }
        public SearchDocument(string id, long sender, string story) {
            Id = id;
            Sender = sender;
            Story = story;
        }
    
    }
    static readonly ILogger<SearchDao> logger = new LoggerFactory().CreateLogger<SearchDao>();

    private static readonly HttpClient client = new() {
        BaseAddress = new Uri($"http://{Environment.GetEnvironmentVariable("SEARCH_HOST") ?? "elasticsearch"}"),
    };

    public async Task<bool> IndexAsync(string id, string sender, string story)
    {
        SearchDocument s = new(id, long.Parse(sender), story);
        JsonContent request = JsonContent.Create<SearchDocument>(s);
        var r = await client.PutAsync($"/feed/stories/{id}", request);
        return r.IsSuccessStatusCode;
    }

    public async Task<IEnumerable<string>> SearchAsync(string keywords)
    {
        List<string> rv = new();
        var resp = await client.GetAsync($"/feed/stories/_search?q={keywords}");
        if (resp.IsSuccessStatusCode) {
            var d = await resp.Content.ReadAsStringAsync();
            var jd = JsonSerializer.Deserialize<Dictionary<string, object>>(d);
            if (jd != null && jd.ContainsKey("hits")) {
                var hits = jd["hits"] as Dictionary<string, object>;
                if (hits != null && hits.ContainsKey("hits")) {
                    var hitsList = hits["hits"] as List<Dictionary<string, object>>;
                    if (hitsList != null) {
                        foreach (var h in hitsList) {
                            if (h != null && h.ContainsKey("_source")) {
                                var s = h["_source"] as Dictionary<string, object>;
                                if (s != null && s.ContainsKey("sender")) {
                                    object o = s["sender"];
                                    if (o != null) {
                                        rv.Add(o.ToString());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return rv;
    }
}