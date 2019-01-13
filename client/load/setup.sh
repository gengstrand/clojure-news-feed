curl -X PUT http://elasticsearch:9200/performance
curl -i -X POST --url http://kong-proxy:8001/services \
--data 'name=feed' \
--data 'url=http://feed:8080/'
curl -i -X POST --url http://kong-proxy:8001/services/feed/routes \
--data 'methods[]=GET&methods[]=PUT&methods[]=POST&methods[]=OPTIONS'
curl -i -X POST --url http://kong-proxy:8001/plugins \
--data 'name=http-log' \
--data "config.http_endpoint=http://kong-logger:8888" \
--data 'config.method=PUT' \
--data 'config.timeout=1000' \
--data 'config.keepalive=1000'
sleep 30
java -jar /usr/app/load-0.1.0-SNAPSHOT-standalone.jar
