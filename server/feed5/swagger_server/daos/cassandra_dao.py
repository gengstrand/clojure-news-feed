from cassandra import ConsistencyLevel
from cassandra.cluster import Cluster, Session
from cassandra.query import dict_factory
from flask import current_app

cassandraSession = None
cassandraStatements = {}

class CassandraDAO:

    def session(self) -> Session:
        global cassandraSession
        if cassandraSession is None:
            cluster = Cluster([current_app.config['NOSQL_HOST']])
            cassandraSession = cluster.connect(current_app.config['NOSQL_KEYSPACE'])
            cassandraSession.row_factory = dict_factory
        return cassandraSession

    def level(self, name: str) -> ConsistencyLevel:
        if name == 'one':
            return ConsistencyLevel.ONE
        if name == 'quorem':
            return ConsistencyLevel.QUOREM
        return ConsistencyLevel.ALL

    def prepare(self, key: str, cql: str):
        global cassandraStatements
        if key not in cassandraStatements.keys():
            cassandraStatements[key] = self.session().prepare(cql)

    def execute(self, key, binding, handler = None):
        global cassandraStatements
        if key in cassandraStatements:
            ps = cassandraStatements[key]
            bs = ps.bind(binding)
            bs.consistencyLevel = self.level(current_app.config['NOSQL_CONSISTENCY_LEVEL'])
            if handler is None:
                self.session().execute_async(bs)
            else:
                return list(map(handler, self.session().execute(bs)))
