import hosts
print '''
cd /home/ec2-user
sudo nohup java -jar /home/ec2-user/news-feed-performance-assembly-1.0.jar {kafka} 127.0.0.1 summary >/dev/null &
sudo nohup java -jar /home/ec2-user/kong-logger-service-1.0.0-SNAPSHOT-fat.jar -conf /home/ec2-user/my-conf.json >/dev/null & 
'''.format(kafka=hosts.settings['kafka'])
