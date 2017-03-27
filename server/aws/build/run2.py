import hosts
print '''
export APP_CONFIG=/home/{user}/settings.properties
java -DLOG_DIR=/tmp \\
     -DLOG_LEVEL=WARN \\
     -jar /home/{user}/news-feed-assembly-0.1.0-SNAPSHOT.jar 
'''.format(feed=hosts.settings['feed'],
           user=hosts.settings['user'])

