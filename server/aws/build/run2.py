import hosts
print '''
cd /home/ec2-user
export APP_CONFIG=/home/ec2-user/settings.properties
java -DLOG_DIR=/tmp \\
     -DLOG_LEVEL=WARN \\
     -Dcom.sun.management.jmxremote.port=9990 \\
     -Dcom.sun.management.jmxremote.ssl=false \\
     -Dcom.sun.management.jmxremote.authenticate=false \\
     -Dcom.sun.management.jmxremote.local.only=false \\
     -Djava.rmi.server.hostname={feed} \\
     -jar /home/ec2-user/news-feed-assembly-0.1.0-SNAPSHOT.jar 
'''.format(feed=hosts.settings['feed'])

