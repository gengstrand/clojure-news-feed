curl -X PUT http://elasticsearch:9200/performance
curl -i -X POST --url http://kong-proxy:8001/apis \
--data 'name=feed' \
--data "upstream_url=http://feed:8080/" \
--data 'methods=GET,PUT,POST,OPTIONS'
curl -i -X POST --url http://kong-proxy:8001/apis/feed/plugins \
--data 'name=http-log' \
--data "config.http_endpoint=http://kong-logger:8888" \
--data 'config.method=PUT' \
--data 'config.timeout=1000' \
--data 'config.keepalive=1000'
sleep 30
java -jar /usr/app/load-0.1.0-SNAPSHOT-standalone.jar
