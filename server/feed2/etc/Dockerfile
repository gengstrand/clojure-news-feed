FROM ubuntu
RUN \
  apt-get update && \
  apt-get install -y openjdk-7-jdk && \
  rm -rf /var/lib/apt/lists/* && \
  mkdir /usr/app
WORKDIR /usr/app
ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64
ENV APP_CONFIG /usr/app/settings.properties
COPY settings.properties /usr/app/
COPY news-feed-assembly-0.1.0-SNAPSHOT.jar /usr/app/
EXPOSE 8080
CMD ["java", "-DLOG_DIR=/tmp", "-DLOG_LEVEL=warn", "-jar", "news-feed-assembly-0.1.0-SNAPSHOT.jar"]

