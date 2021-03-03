FROM openjdk:11
RUN \
  apt-get update && \
  mkdir /usr/app
WORKDIR /usr/app
COPY target/feed-0.1.0-SNAPSHOT-standalone.jar /usr/app
EXPOSE 8080
CMD ["java", "-jar", "/usr/app/feed-0.1.0-SNAPSHOT-standalone.jar"]

