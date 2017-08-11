function extractHostPort {
    SERVICE=$1
    URL=$(minikube service $SERVICE --url | head -n 1)
    HOST=$(echo $URL | sed -e '1,$s/http:\/\///' -e '1,$s/:[0-9]*$//')
    PORT=$(echo $URL | sed -e '1,$s/^.*://')
}
extractHostPort mysql
mysql -h $HOST -P $PORT -u root -pfeed <<EOF 
create user 'feed'@'%' identified by 'feed1234';
create database feed;
grant all on feed.* to 'feed'@'%';
EOF
sleep 10
mysql -h $HOST -P $PORT -u feed -pfeed1234 --database=feed <../feed/etc/schema.mysql.sql
extractHostPort cassandra
cqlsh $HOST $PORT <../feed/etc/schema.cassandra.sql
extractHostPort elasticsearch
curl -XPOST http://${HOST}:${PORT}/feed -d '{
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
