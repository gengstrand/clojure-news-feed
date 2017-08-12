import hosts
print '''
SQLALCHEMY_DATABASE_URI = 'mysql+mysqlconnector://feed:feed1234@{mysql}/feed'
CACHE_HOST = '{redis}'
NOSQL_HOST = '{cassandra}'
MESSAGE_BROKER = '127.0.0.1'
SEARCH_HOST = '{elastic}'
'''.format(redis=hosts.settings['redis'],
           cassandra=hosts.settings['cassandra'],
           elastic=hosts.settings['elastic'], 
           mysql=hosts.settings['mysql'])
