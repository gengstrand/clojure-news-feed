FROM java:8-jre
RUN \
  apt-get update && \
  mkdir /usr/app
WORKDIR /usr/app
COPY my-conf.json /usr/app/
COPY target/kong-logger-service-1.0.0-SNAPSHOT-fat.jar /usr/app/
EXPOSE 8888
CMD ["java", "-jar", "/usr/app/kong-logger-service-1.0.0-SNAPSHOT-fat.jar", "-conf", "/usr/app/my-conf.json"]

