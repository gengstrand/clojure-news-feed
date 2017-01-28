python cassandra.py >../cassandra/cassandra.yaml
python config.py >../feed3/config.yml
python envlist.py >../feed4/env.list
python configclj.py >../feed/config.clj
python redis.py >../redis/redis.conf
python kong.py >../kong/kong.yml
python settings.properties.py >../feed2/settings.properties
python run2.py >../feed2/run.sh
python run3.py >../feed3/run.sh
python runload2.py >../load/runload2.sh
python runload3.py >../load/runload3.sh
python kongSetup.py >../kong/setup.sh
python runlogger.py >../elasticsearch/run.sh
python my-conf.py >../elasticsearch/my-conf.json
python elastic.py >../elasticsearch/install.sh
chmod a+x ../feed2/run.sh ../feed3/run.sh ../kong/setup.sh ../elasticsearch/install.sh  ../load/*.sh
python copy.py >../copy.sh
