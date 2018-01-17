FROM java:8-jre
RUN \
  apt-get update && \
  mkdir /usr/app
WORKDIR /usr/app
COPY target/load-0.1.0-SNAPSHOT-standalone.jar /usr/app/
CMD ["java", "-jar", "/usr/app/load-0.1.0-SNAPSHOT-standalone.jar"]