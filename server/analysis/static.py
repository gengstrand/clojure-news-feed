import os
import csv
import statistics

if __name__ == '__main__':
    ftc = {}
    ftloc = {}
    skip = ['git', 'target', 'bin', 'node_modules', 'dart_tool', 'build', 'terraform']
    for root, dirs, files in os.walk("../.."):
        if len([1 for d in skip if d in root]) > 0:
            continue
        print(root)
        for name in files:
            np = name.split('.')
            ft = np[-1]
            if len(ft) <= 5 and not ft.endswith('~') and not ft.endswith('#'):
                loc = 0
                try:
                    with open(root + '/' + name, 'r') as f:
                        loc = len(f.readlines())
                    if ft in ftc:
                        ftc[ft] = ftc[ft] + 1
                        ftloc[ft].append(loc)
                    else:
                        ftc[ft] = 1
                        ftloc[ft] = [ loc ]
                except:
                    pass
        with open('count-file-types.csv', 'w', newline='') as csvfile:
            fn = ['type', 'count', 'avg_loc', 'std_loc']
            report = csv.DictWriter(csvfile, fieldnames=fn)
            report.writeheader()
            for k, v in ftc.items():
                row = {}
                row['type'] = k
                row['count'] = v
                row['avg_loc'] = statistics.mean(ftloc[k])
                row['std_loc'] = statistics.stdev(ftloc[k]) if len(ftloc[k]) >= 2 else 0
                report.writerow(row)
