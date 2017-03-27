function uninstall-if-present {
  P=$1
  T=`yum list installed $P | grep $P | wc -l`
  if [ $T -ge 1 ]
  then
    sudo yum -y remove $P
  fi
}
sudo yum -y update
uninstall-if-present java-1.7.0-openjdk
sudo yum -y install java-1.8.0-openjdk
gunzip elasticsearch-2.3.3.tar.gz 
tar -xf elasticsearch-2.3.3.tar 
cp elasticsearch.yml elasticsearch-2.3.3/config
cd elasticsearch-2.3.3
bin/elasticsearch -d
sleep 30
curl -XPUT http://localhost:9200/performance
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
cd ..
gunzip kibana-4.5.1-linux-x64.tar.gz
tar -xf kibana-4.5.1-linux-x64.tar
cd kibana-4.5.1-linux-x64
sudo nohup /home/$USER/kibana-4.5.1-linux-x64/bin/kibana >/dev/null &
echo "elastic search and kibana started. You now need to sh ./run.sh"
