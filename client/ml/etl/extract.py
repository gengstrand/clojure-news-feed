import sys
import dateutil.parser
from elasticsearch import Elasticsearch
from elasticsearch.helpers import scan
if __name__ == '__main__':
    if len(sys.argv) < 4:
        print ('usage: python script feed cloud elasticsearch')
        sys.exit(-1)
    es = Elasticsearch(sys.argv[3])
    cloud = sys.argv[2]
    feed = sys.argv[1]
    res = es.search(index = "performance",
                    scroll = "10m",
                    search_type = "scan",
                    size = 5000, 
                    body = {"query": {"match_all": {}}})
    sid = res['_scroll_id']
    scroll_size = res['hits']['total']
    print ('year,month,day,hour,minute,second,cloud,feed,entity,operation,status,duration')
    while scroll_size > 0:
        res = es.scroll(scroll_id = sid, scroll = "10m")
        sid = res['_scroll_id']
        scroll_size = len(res['hits']['hits'])
        for hit in res['hits']['hits']:
            s = hit['_source']
            ts = dateutil.parser.parse(s['ts'])
            print ("{},{},{},{},{},{},{},{},{},{},{},{}".format(ts.year, ts.month, ts.day, ts.hour, ts.minute, ts.second, cloud, feed, s['entity'], s['operation'], s['status'], s['duration']))
