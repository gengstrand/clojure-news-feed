FEED_URL=$(minikube service feed --url)
KONG_URL=$(minikube service kong-admin --url)
ELASTIC_URL=$(minikube service elasticsearch --url)
KONG_LOGGER_URL=$(minikube service kong-logger --url)
curl -X PUT ${ELASTIC_URL}/performance
curl -i -X POST --url ${KONG_URL}/apis \
--data 'name=feed' \
--data "upstream_url=${FEED_URL}/" \
--data 'methods=GET,PUT,POST,OPTIONS'
curl -i -X POST --url ${KONG_URL}/apis/feed/plugins \
--data 'name=http-log' \
--data "config.http_endpoint=${KONG_LOGGER_URL}" \
--data 'config.method=PUT' \
--data 'config.timeout=1000' \
--data 'config.keepalive=1000'
