import hosts
print '''
{{:sql-host "{mysql}"
 :sql-db-name "feed"
 :sql-db-user "feed"
 :sql-db-password "feed1234"
 :sql-vendor "mysql"
 :nosql-host "{cassandra}"
 :nosql-consistency "one" 
 :nosql-ttl 7776000 
 :cache-host "{redis}"
 :messaging-host "{kafka}"
 :search-host "http://{elastic}:9200/feed/stories"}}
'''.format(redis=hosts.settings['redis'],
           cassandra=hosts.settings['cassandra'],
           elastic=hosts.settings['elastic'], 
           kafka=hosts.settings['kafka'], 
           mysql=hosts.settings['mysql'])
