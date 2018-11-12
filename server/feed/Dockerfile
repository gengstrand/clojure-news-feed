FROM java:8-jdk
RUN \
  apt-get update -y && \
  apt-get install -y git && \
  apt-get install -y wget && \
  mkdir -p /usr/app/bin && \
  mkdir -p /home/glenn/.m2
WORKDIR /usr/app
ADD http://mirror.cogentco.com/pub/apache/maven/maven-3/3.6.0/binaries/apache-maven-3.6.0-bin.tar.gz /usr/app/
ADD https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein /usr/app/bin/
COPY config.clj /usr/app/
ENV PATH $PATH:/usr/app/apache-maven-3.6.0/bin:/usr/app/bin:/usr/lib/jvm/java-7-openjdk-amd64/bin
ENV APP_CONFIG /usr/app/config.clj
RUN \
  tar -xzf apache-maven-3.6.0-bin.tar.gz && \
  chmod a+x /usr/app/bin/lein && \
  git clone http://github.com/gengstrand/clojure-news-feed.git && \
  cd clojure-news-feed/server/support && \
  mvn clean install && \
  cd /usr/app && \
  cp -Rf /root/.m2/* /home/glenn/.m2
EXPOSE 8080
CMD ["sh", "/usr/app/clojure-news-feed/server/feed/etc/run.sh"]
