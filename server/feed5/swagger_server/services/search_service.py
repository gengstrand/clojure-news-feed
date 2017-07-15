import datetime, time, random

from elasticsearch import Elasticsearch
from flask import current_app

es = None

class SearchService:
    
    def elastic(self) -> Elasticsearch:
        global es
        if es is None:
            es = Elasticsearch([current_app.config['SEARCH_HOST']])
        return es

    def key(self) -> str:
        return str(time.time()) + '-' + str(random.randint(0, 1000000))

    def create(self, sender: int, story: str):
        i = current_app.config['SEARCH_INDEX']
        dt = current_app.config['SEARCH_TYPE']
        k = self.key()
        pk = str(sender) + '-' + k
        d = {'id': k, 'sender': sender, 'story': story}
        self.elastic().index(index=i, doc_type=dt, id=pk, body=d)

    def fetch(self, term):
        i = current_app.config['SEARCH_INDEX']
        dt = current_app.config['SEARCH_TYPE']
        b = {'query': { 'match': { 'story': term } } }
        return self.elastic().search(index=i, doc_type=dt, body=b)['hits']['hits']

    def extract(self, hit):
        return hit['_source']['sender']

    def search(self, term):
        return map(self.extract, self.fetch(term))
