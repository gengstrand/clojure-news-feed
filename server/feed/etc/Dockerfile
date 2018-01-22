FROM ubuntu
RUN \
  apt-get update && \
  apt-get -y install mysql-client && \
  apt-get -y install curl && \
  apt-get -y install python && \
  mkdir -p /usr/app 
WORKDIR /usr/app
COPY schema.mysql.sql /usr/app/
COPY schema.cassandra.sql /usr/app/
COPY initCluster.sh /usr/app/
ADD http://apache.claz.org/cassandra/2.2.11/apache-cassandra-2.2.11-bin.tar.gz /usr/app/
ENV PATH $PATH:/usr/app/apache-cassandra-2.2.11/bin
