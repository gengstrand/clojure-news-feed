## Additional notes on setup

For the most part, standing up this service has the same dependencies as
standing up either the Clojure or Scala versions with one notable exception.
This service uses Elastic Search, instead of Solr, for its keyword based search capability.

Why is that? The other services use the support library which depends on SolrJ 
which depends on Jetty version 9.2.11 while this service needs Dropwizard version 1 
which depends on Jetty version 9.3.9 so we have a version conflict issue.

### running the elastic search service

```bash
bin/elasticsearch
```

This will create the index for the initial setup.

```bash
curl -XPOST http://localhost:9200/feed -d '{
    "settings" : {
        "number_of_shards" : 1
    },
    "mappings" : {
        "stories" : {
            "properties" : {
                "id" : { "type" : "string", "index" : "not_analyzed" },
                "sender" : { "type" : "string", "index" : "not_analyzed" },
                "story" : { "type" : "string"}
            }
        }
    }
}'
```

### How to test this service

This service is a little bit different from the previous services in that the POST input is in JSON format now.

```bash

curl -H "Content-Type: application/json" -d '{"name":"testing dropwizard"}' http://localhost:8080/participant/new

curl -H "Content-Type: application/json" -d '{"from":4320,"to":3246}' http://localhost:8080/friends/new

curl -H "Content-Type: application/json" -d '{"id":4320,"subject":"testing for dropwizard","story":"How does the new dropwizard version of the news feed micro-service work?"}' http://localhost:8080/outbound/new

curl -X POST -g 'http://localhost:8080/outbound/search?keywords=dropwizard'

```