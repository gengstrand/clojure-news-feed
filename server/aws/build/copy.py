import hosts
print '''
PEM_FILE=$1
scp -i $PEM_FILE cassandra/* ec2-user@{cassandra}:/home/ec2-user
scp -i $PEM_FILE kafka/* ec2-user@{kafka}:/home/ec2-user
scp -i $PEM_FILE redis/* ec2-user@{redis}:/home/ec2-user
scp -i $PEM_FILE elasticsearch/* ec2-user@{elastic}:/home/ec2-user
scp -i $PEM_FILE kong/* ec2-user@{kong}:/home/ec2-user
scp -i $PEM_FILE feed/* ec2-user@{feed}:/home/ec2-user
# scp -i $PEM_FILE feed2/* ec2-user@{feed}:/home/ec2-user
# scp -i $PEM_FILE feed3/* ec2-user@{feed}:/home/ec2-user
# scp -i $PEM_FILE feed4/* ec2-user@{feed}:/home/ec2-user
scp -i $PEM_FILE load/* ec2-user@{load}:/home/ec2-user
cd ~/git/clojure-news-feed/server/feed/etc
mysql -h {mysql} -u feed -p feed <schema.mysql.sql
# cqlsh {cassandra} <schema.cassandra.sql
'''.format(redis=hosts.settings['redis'],
           cassandra=hosts.settings['cassandra'],
           elastic=hosts.settings['elastic'], 
           feed=hosts.settings['feed'], 
           load=hosts.settings['load'], 
           kong=hosts.settings['kong'], 
           kafka=hosts.settings['kafka'], 
           mysql=hosts.settings['mysql'])
