import hosts
print '''
curl -i -X POST --url http://localhost:8001/apis \\
--data 'name=feed' \\
--data "upstream_url=http://{feed}:8080/" \\
--data 'request_path=/'
curl -i -X POST --url http://localhost:8001/apis/feed/plugins \\
--data 'name=http-log' \\
--data "config.http_endpoint=http://{elastic}:8888" \\
--data 'config.method=PUT' \\
--data 'config.timeout=1000' \\
--data 'config.keepalive=1000'
'''.format(feed=hosts.settings['feed'],
           elastic=hosts.settings['elastic'])
