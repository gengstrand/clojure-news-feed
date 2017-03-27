import hosts
print '''
sudo nohup java -jar /home/{user}/news-feed-performance-assembly-1.0.jar {kafka} 127.0.0.1 summary >/dev/null &
sudo nohup java -jar /home/{user}/kong-logger-service-1.0.0-SNAPSHOT-fat.jar -conf /home/{user}/my-conf.json >/dev/null & 
'''.format(kafka=hosts.settings['kafka'],
           user=hosts.settings['user'])
