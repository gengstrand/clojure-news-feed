class Config(object):
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    SQLALCHEMY_POOL_SIZE = 18
    CACHE_PORT = 6379
    NOSQL_KEYSPACE = 'activity'
    NOSQL_CONSISTENCY_LEVEL = 'one'
    MESSAGE_TOPIC = 'feed'
    SEARCH_INDEX = 'feed'
    SEARCH_TYPE = 'stories'
