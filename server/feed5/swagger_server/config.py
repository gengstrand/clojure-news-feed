class Config(object):
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    CACHE_PORT = 6379
    NOSQL_KEYSPACE = 'activity'
    NOSQL_CONSISTENCY_LEVEL = 'one'
    MESSAGE_TOPIC = 'feed'
