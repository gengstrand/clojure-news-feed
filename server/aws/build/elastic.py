import hosts
print '''
function uninstall-if-present {
  P=$1
  T=`yum list installed $P | grep $P | wc -l`
  if [ $T -ge 1 ]
  then
    sudo yum -y remove $P
  fi
}
'''
print '''
sudo yum -y update
uninstall-if-present java-1.7.0-openjdk
sudo yum -y install java-1.8.0-openjdk
curl -XPUT http://{elastic}:9200/performance
curl -XPOST http://{elastic}:9200/feed -d '{{
    "settings" : {{
        "number_of_shards" : 1
    }},
    "mappings" : {{
        "stories" : {{
            "properties" : {{
                "id" : {{ "type" : "string", "index" : "not_analyzed" }},
                "sender" : {{ "type" : "string", "index" : "not_analyzed" }},
                "story" : {{ "type" : "string"}}
            }}
        }}
    }}
}}'
sudo nohup java -jar /home/{user}/news-feed-performance-assembly-1.0.jar {kafka} {elastic} summary >/dev/null &
sudo nohup java -jar /home/{user}/kong-logger-service-1.0.0-SNAPSHOT-fat.jar -conf /home/{user}/my-conf.json >/dev/null & 
'''.format(elastic=hosts.settings['elastic'], 
           kafka=hosts.settings['kafka'],
           user=hosts.settings['user'])
