mysql -h mysql -u root -pfeed <<EOF 
create user 'feed'@'%' identified by 'feed1234';
create database feed;
grant all on feed.* to 'feed'@'%';
EOF
sleep 10
mysql -h mysql -u feed -pfeed1234 --database=feed <schema.mysql.sql
cqlsh cassandra <schema.cassandra.sql
curl -XPOST http://elasticsearch:9200/feed -d '{
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
