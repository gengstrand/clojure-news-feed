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
        BaseAddress = new Uri($"http://{Environment.GetEnvironmentVariable("SEARCH_HOST") ?? "elasticsearch"}:9200/"),
    };

    static readonly JsonSerializerOptions jo = new() {
       PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
    };
    
    public async Task<bool> IndexAsync(string id, string sender, string story)
    {
        SearchDocument s = new(id, long.Parse(sender), story);
	    var request = new StringContent(JsonSerializer.Serialize(s, jo), System.Text.Encoding.UTF8, "application/json");
        var r = client.PutAsync($"feed/stories/{id}", request).Result;
        return r.IsSuccessStatusCode;
    }

    public async Task<IEnumerable<string>> SearchAsync(string keywords)
    {
        List<string> rv = new();
        var resp = client.GetAsync($"feed/stories/_search?q={keywords}").Result;
        if (resp.IsSuccessStatusCode) {
            var d = await resp.Content.ReadAsStringAsync();
            var jd = JsonSerializer.Deserialize<Dictionary<string, object>>(d);
            if (jd != null) {
                foreach(KeyValuePair<string, object> kvp in jd) {
                    if (kvp.Key == "hits") {
                        var oh = (JsonElement)kvp.Value;
                        var ih = oh.GetProperty("hits");
                        foreach(var ihi in ih.EnumerateArray()) {
                            var ihsrc = ihi.GetProperty("_source");
                            var ihsnd = ihsrc.GetProperty("sender");
                            var ihsv = ihsnd.GetInt32();
                            rv.Add(ihsv.ToString());
                        }
                    }
                }
            }
        }
        return rv;
    }
}