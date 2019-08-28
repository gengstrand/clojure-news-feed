import datetime, time, random, sys

from elasticsearch import Elasticsearch
from flask import current_app
from queue import Queue
from threading import Thread

es = None
q = Queue(maxsize=0)

class NewsFeedItem:
    def __init__(self, sender: int, story: str, es_index: str, es_type: str, es_host: str):
        self.sender = sender
        self.story = story
        self.es_index = es_index
        self.es_type = es_type
        self.es_host = es_host

def key() -> str:
    return str(time.time()) + '-' + str(random.randint(0, 1000000))

def elastic(es_host: str) -> Elasticsearch:
    global es
    if es is None:
        es = Elasticsearch([es_host])
    return es

def save_doc():
    global q
    while True:
        try:
            item = q.get()
            i = item.es_index
            dt = item.es_type
            k = key()
            pk = str(item.sender) + '-' + k
            d = {'id': k, 'sender': item.sender, 'story': item.story}
            elastic(item.es_host).index(index=i, doc_type=dt, id=pk, body=d)
            q.task_done()
        except:
            print("Unexpected error:", sys.exc_info()[0])

for i in range(3):
    worker = Thread(target=save_doc)
    worker.setDaemon(True)
    worker.start()

class SearchService:

    def create(self, sender: int, story: str):
        global q
        q.put(NewsFeedItem(sender, story, current_app.config['SEARCH_INDEX'], current_app.config['SEARCH_TYPE'], current_app.config['SEARCH_HOST']))

    def fetch(self, term):
        i = current_app.config['SEARCH_INDEX']
        dt = current_app.config['SEARCH_TYPE']
        b = {'query': { 'match': { 'story': term } } }
        return elastic(current_app.config['SEARCH_HOST']).search(index=i, doc_type=dt, body=b)['hits']['hits']

    def extract(self, hit):
        return hit['_source']['sender']

    def search(self, term):
        return map(self.extract, self.fetch(term))
