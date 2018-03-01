FROM java:8-jre
RUN \
  apt-get update && \
  mkdir /usr/app
WORKDIR /usr/app
ENV APP_CONFIG /usr/app/settings.properties
COPY settings.properties /usr/app/
COPY news-feed-assembly-0.1.0-SNAPSHOT.jar /usr/app/
EXPOSE 8080
CMD ["java", "-DLOG_DIR=/tmp", "-DLOG_LEVEL=warn", "-jar", "news-feed-assembly-0.1.0-SNAPSHOT.jar"]

