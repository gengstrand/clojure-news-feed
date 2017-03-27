import hosts
print '''
scp -i {cert} cassandra/* {user}@{cassandra}:/home/{user}
scp -i {cert} kafka/* {user}@{kafka}:/home/{user}
scp -i {cert} redis/* {user}@{redis}:/home/{user}
scp -i {cert} elasticsearch/* {user}@{elastic}:/home/{user}
# scp -i {cert} solr/* {user}@{solr}:/home/{user}
scp -i {cert} kong/* {user}@{kong}:/home/{user}
scp -i {cert} feed/* {user}@{feed}:/home/{user}
# scp -i {cert} feed2/* {user}@{feed}:/home/{user}
# scp -i {cert} feed3/* {user}@{feed}:/home/{user}
# scp -i {cert} feed4/* {user}@{feed}:/home/{user}
scp -i {cert} load/* {user}@{load}:/home/{user}
cd ~/git/clojure-news-feed/server/feed/etc
# mysql -h {mysql} -u root -p
# create user 'feed'@'%' identified by 'feed1234';
# create database feed;
# grant all on feed.* to 'feed'@'%';
mysql -h {mysql} -u feed -p feed <schema.mysql.sql
# cqlsh {cassandra} <schema.cassandra.sql
'''.format(redis=hosts.settings['redis'],
           cassandra=hosts.settings['cassandra'],
           elastic=hosts.settings['elastic'], 
           solr=hosts.settings['solr'], 
           feed=hosts.settings['feed'], 
           load=hosts.settings['load'], 
           kong=hosts.settings['kong'], 
           kafka=hosts.settings['kafka'], 
           mysql=hosts.settings['mysql'],
           user=hosts.settings['user'],
           cert=hosts.settings['cert'])
