import hosts
print '''
cd /home/ec2-user
sudo yum -y update
sudo yum -y remove java-1.7.0-openjdk
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
sudo nohup java -jar /home/ec2-user/news-feed-performance-assembly-1.0.jar {kafka} {elastic} summary >/dev/null &
sudo nohup java -jar /home/ec2-user/kong-logger-service-1.0.0-SNAPSHOT-fat.jar -conf /home/ec2-user/my-conf.json >/dev/null & 
'''.format(elastic=hosts.settings['elastic'], 
           kafka=hosts.settings['kafka'])
