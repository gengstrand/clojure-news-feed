sudo yum -y update
sudo yum -y remove java-1.7.0-openjdk
sudo yum -y install java-1.8.0-openjdk
gunzip solr-5.3.1.tar.gz 
tar -xf solr-5.3.1.tar 
cd solr-5.3.1
bin/solr start
bin/solr create_core -c outbound
bin/solr stop
cd ..
gunzip solrConfig.tar.gz 
tar -xf solrConfig.tar 
cp -Rf solr/example/multicore/outbound/* solr-5.3.1/server/solr/outbound
cd solr-5.3.1
bin/solr start
