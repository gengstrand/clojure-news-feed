sudo yum -y update
sudo yum -y remove java-1.7.0-openjdk
sudo yum -y install java-1.8.0-openjdk
gunzip apache-cassandra-2.2.6-bin.tar.gz
tar -xf apache-cassandra-2.2.6-bin.tar
cd apache-cassandra-2.2.6
cp ../cassandra.yaml conf/cassandra.yaml
bin/cassandra

