import hosts
print '''
# mysql
jdbc_vendor=mysql
jdbc_driver=com.mysql.jdbc.Driver
jdbc_url=jdbc:mysql://{mysql}/feed
jdbc_user=feed
jdbc_password=feed1234
jdbc_min_pool_size=3
jdbc_max_pool_size=18
jdbc_acquire_increment=1
jdbc_max_statements=36
# postgresql
# jdbc_vendor=postgresql
# jdbc_driver=org.postgresql.Driver
# jdbc_url=jdbc:postgresql://localhost/feed
nosql_host={cassandra}
nosql_keyspace=activity
nosql_read_consistency_level=one
nosql_ttl=7776000
messaging_brokers={kafka}:9092
zookeeper_servers={kafka}:2181
search_host={solr}
search_port=9200
search_path=feed/stories
cache_host={redis}
cache_port=6379
cache_timeout=60
'''.format(redis=hosts.settings['redis'],
           cassandra=hosts.settings['cassandra'],
           solr=hosts.settings['solr'], 
           feed=hosts.settings['feed'], 
           kafka=hosts.settings['kafka'], 
           mysql=hosts.settings['mysql'])
