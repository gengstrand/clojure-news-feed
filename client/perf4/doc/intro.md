## building the logger service

```bash
mvn package
```

## running the elastic search service

```bash
bin/elasticsearch
```

This will create the index for the initial setup.

```bash
curl -XPUT http://localhost:9200/performance
```

## running the logger service

```bash
java -jar target/kong-logger-service-1.0.0-SNAPSHOT-fat.jar -conf src/conf/my-conf.json
```

## testing the logger service

```bash
curl -XPUT http://localhost:8888 -d '
{
   "request" : {
     "uri" : "/participant/new",
     "method" : "post"
   },
   "response" : {
     "status" : 200
   },
   "latencies" : {
     "request" : 23
   }
}'

```
## setting up kong

```bash
kong start
curl -i -X POST --url http://localhost:8001/apis \
--data 'name=feed' \
--data 'upstream_url=http://localhost:8080/' \
--data 'request_path=/'
curl -i -X POST --url http://localhost:8001/apis/feed/plugins \
--data 'name=http-log' \
--data 'config.http_endpoint=http://localhost:8888' \
--data 'config.method=PUT' \
--data 'config.timeout=1000' \
--data 'config.keepalive=1000'
```

