import hosts
print '''
ulimit -n 4096
java -Dcom.sun.management.jmxremote.port=9990 \\
     -Dcom.sun.management.jmxremote.ssl=false \\
     -Dcom.sun.management.jmxremote.authenticate=false \\
     -Dcom.sun.management.jmxremote.local.only=false \\
     -Djava.rmi.server.hostname={feed} \\
     -jar /home/{user}/newsfeed-dropwizard-1.0.0-SNAPSHOT.jar server /home/{user}/config.yml
'''.format(feed=hosts.settings['feed'],
           user=hosts.settings['user'])
