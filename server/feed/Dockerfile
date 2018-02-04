FROM andreptb/oracle-java:7
RUN \
  mkdir -p /usr/app/bin && \
  mkdir -p /home/glenn/.m2
WORKDIR /usr/app
ADD http://www-eu.apache.org/dist/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.tar.gz /usr/app/
ADD https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein /usr/app/bin/
COPY config.clj /usr/app/
COPY run.sh /usr/app/bin/
ENV PATH $PATH:/usr/app/apache-maven-3.5.2/bin:/usr/app/bin:/usr/lib/jvm/java-7-openjdk-amd64/bin
ENV APP_CONFIG /usr/app/config.clj
RUN \
  apk update && \
  apk upgrade && \
  apk add git && \
  apk add wget && \
  git clone http://github.com/gengstrand/clojure-news-feed.git && \
  cd clojure-news-feed/server/support && \
  mvn clean install && \
  cd /usr/app && \
  cp -Rf /root/.m2/* /home/glenn/.m2
EXPOSE 8080
CMD ["bash", "run.sh"]
