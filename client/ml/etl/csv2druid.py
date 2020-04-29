import csv
import sys
import json
import datetime
with open(sys.argv[1]) as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        ts = datetime.datetime(int(row['year']), int(row['month']), int(row['day']), int(row['hour']), int(row['minute']), int(row['second']))
        o = {}
        o['ts'] = ts.strftime('%Y-%m-%dT%H:%M:%S.000Z')
        o['cloud'] = row['cloud']
        o['feed'] = row['feed']
        o['entity'] = row['entity']
        o['operation'] = row['operation']
        o['status'] = int(row['status'])
        o['duration'] = int(row['duration'])
        print(json.dumps(o))
