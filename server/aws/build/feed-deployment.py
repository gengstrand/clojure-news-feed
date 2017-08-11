import hosts
print '''
apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  creationTimestamp: null
  name: feed
spec:
  replicas: 1
  template:
    metadata:
      creationTimestamp: null
      labels:
        io.kompose.service: feed
    spec:
      containers:
      - env:
        - name: MYSQL_HOST
          value: {mysql}
        - name: NOSQL_HOST
          value: {cassandra}
        - name: REDIS_HOST
          value: {redis}
        - name: SEARCH_HOST
          value: {elastic}
        image: feed4:1.0
        name: feed
        ports:
        - containerPort: 8080
      restartPolicy: Always
'''.format(redis=hosts.settings['redis'],
           cassandra=hosts.settings['cassandra'],
           elastic=hosts.settings['elastic'], 
           mysql=hosts.settings['mysql'])
