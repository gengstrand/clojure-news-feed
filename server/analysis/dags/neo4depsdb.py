from neo4j import GraphDatabase
from typing import List

class CodeDependency:
    def __init__(self, org: str, repo: str, keyword: str = '', language: str = ''):
        self._org = org
        self._repo = repo
        self._keyword = keyword
        self._language = language

    @property
    def org(self) -> str:
        return self._org

    @property
    def repo(self) -> str:
        return self._repo

    @property
    def keyword(self) -> str:
        return self._keyword

    @property
    def language(self) -> str:
        return self._language

class Neo4JDAO:
    """base class for Neo 4 J DAO"""
    def __init__(self, host: str, user: str, password: str):
        url = "neo4j://%s:7687" % (host)
        self.driver = GraphDatabase.driver(url, auth=(user, password))

    def upsert(self, hits: List[CodeDependency]):
        with self.driver.session() as session:
            for dep in hits:
                session.execute_write(self.handler, dep)

    def close(self):
        self.driver.close()

class KeywordDependencies(Neo4JDAO):
    def __init__(self, host: str, password: str):
        super().__init__(host, 'neo4j', password)
        upsert_stmt = "MERGE (o: Org {name: '%s'}) MERGE (r: Repo {name: '%s'}) MERGE (k: Keyword {name: '%s'}) MERGE (o)-[:CONTAINS]->(r) MERGE (r)-[:FOUND]->(k)"
        self.handler = lambda tx, dep : tx.run(upsert_stmt % (dep.org, dep.repo, dep.keyword))

class LanguageDependencies(Neo4JDAO):
    def __init__(self, host: str, password: str):
        super().__init__(host, 'neo4j', password)
        upsert_stmt = "MERGE (o: Org {name: '%s'}) MERGE (r: Repo {name: '%s'}) MERGE (l: Language {name: '%s'}) MERGE (o)-[:CONTAINS]->(r) MERGE (r)-[:WRITTEN_IN]->(l)"
        self.handler = lambda tx, dep : tx.run(upsert_stmt % (dep.org, dep.repo, dep.language))
