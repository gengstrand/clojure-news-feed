kubectl port-forward deployment/cassandra 9042:9042 &
kubectl port-forward deployment/mysql 3306:3306 &
kubectl port-forward deployment/redis 6379:6379 &
kubectl port-forward deployment/elasticsearch 9200:9200 &

export MYSQL_HOST=127.0.0.1
export NOSQL_HOST=127.0.0.1
export NOSQL_KEYSPACE=activity
export REDIS_HOST=127.0.0.1
export CACHE_HOST=127.0.0.1
export SEARCH_HOST=127.0.0.1

java -XX:+FlightRecorder -XX:StartFlightRecording=duration=120s,filename=feed8.jfr -XX:+UnlockExperimentalVMOptions -Djava.security.egd=file:/dev/./urandom -jar target/newsfeed-springboot-1.0.0-SNAPSHOT.jar
