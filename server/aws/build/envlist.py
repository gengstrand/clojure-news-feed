import hosts
print '''
MYSQL_HOST={mysql}
NOSQL_HOST={cassandra}
REDIS_HOST={redis}
SEARCH_HOST={elastic}
'''.format(redis=hosts.settings['redis'],
           cassandra=hosts.settings['cassandra'],
           elastic=hosts.settings['elastic'], 
           mysql=hosts.settings['mysql'])
