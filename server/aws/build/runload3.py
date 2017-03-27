import hosts
print '''
sudo nohup java -jar /home/{user}/load-0.1.0-SNAPSHOT-standalone.jar {feed} 3 10 json >/dev/null &
'''.format(feed=hosts.settings['kong'],
           user=hosts.settings['user'])
