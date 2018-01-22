FROM ubuntu
RUN \
  apt-get update && \
  mkdir -p /usr/app/bin && \
  mkdir -p /home/glenn/.m2
WORKDIR /usr/app
COPY jdk-7u79-linux-x64.tar.gz /usr/app/
COPY apache-maven-3.3.9-bin.tar.gz /usr/app/
COPY lein /usr/app/bin/
COPY config.clj /usr/app/
COPY run.sh /usr/app/
ENV PATH $PATH:/usr/app/apache-maven-3.3.9/bin:/usr/app/bin:/usr/app/jdk1.7.0_79/bin
ENV JAVA_HOME /usr/app/jdk1.7.0_79
ENV DEBIAN_FRONTEND noninteractive
ENV APP_CONFIG /usr/app/config.clj
RUN \
  gunzip jdk-7u79-linux-x64.tar.gz && \
  tar -xf jdk-7u79-linux-x64.tar && \
  gunzip apache-maven-3.3.9-bin.tar.gz && \
  tar -xf apache-maven-3.3.9-bin.tar && \
  apt-get -y install git && \
  apt-get -y install wget && \
  git clone https://github.com/gengstrand/clojure-news-feed.git && \
  cd clojure-news-feed/server/support && \
  mvn clean install && \
  cd /usr/app && \
  cp -Rf /root/.m2/* /home/glenn/.m2
EXPOSE 8080
CMD ["bash", "run.sh"]
