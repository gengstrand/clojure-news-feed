import hosts
print '''
cd /home/ec2-user
sudo nohup java -jar /home/ec2-user/load-0.1.0-SNAPSHOT-standalone.jar {host} 3 10 json >/dev/null &
'''.format(feed=hosts.settings['kong'])
