import hosts
print '''
cd /home/ec2-user
export APP_CONFIG=/home/ec2-user/settings.properties
java -DLOG_DIR=/tmp \\
     -DLOG_LEVEL=WARN \\
     -jar /home/ec2-user/news-feed-assembly-0.1.0-SNAPSHOT.jar 
'''.format(feed=hosts.settings['feed'])

