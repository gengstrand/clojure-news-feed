function uninstall-if-present {
  P=$1
  T=`yum list installed $P | grep $P | wc -l`
  if [ $T -ge 1 ]
  then
    sudo yum -y remove $P
  fi
}
sudo yum -y update
uninstall-if-present java-1.7.0-openjdk
sudo yum -y install java-1.8.0-openjdk
gunzip kafka_2.11-0.9.0.1.tar.gz 
tar -xf kafka_2.11-0.9.0.1.tar 
cd kafka_2.11-0.9.0.1
bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
sleep 30
bin/kafka-server-start.sh -daemon config/server.properties
sleep 30
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic feed
