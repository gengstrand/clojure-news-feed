function extractHostPort {
    SERVICE=$1
    URL=$(minikube service $SERVICE --url | head -n 1)
    HOST=$(echo $URL | sed -e '1,$s/http:\/\///' -e '1,$s/:[0-9]*$//')
    PORT=$(echo $URL | sed -e '1,$s/^.*://')
}
echo "are you testing feed 1 or 2 [yN]?"
read response
if [ "$response" == "y" ]
then
    export USE_JSON=false
else
    export USE_JSON=true
fi
extractHostPort feed
export FEED_HOST=$HOST
export FEED_PORT=$PORT
extractHostPort mysql
export MYSQL_HOST=$HOST
export MYSQL_PORT=$PORT
extractHostPort redis
export REDIS_HOST=$HOST
export REDIS_PORT=$PORT
extractHostPort cassandra
export CASSANDRA_HOST=$HOST
export CASSANDRA_PORT=$PORT
extractHostPort elasticsearch
export ELASTIC_HOST=$HOST
export ELASTIC_PORT=$PORT
export MYSQL_USER=feed
export MYSQL_PASSWORD=feed1234
java -jar ../../client/load/target/load-0.1.0-SNAPSHOT-standalone.jar --integration-test

