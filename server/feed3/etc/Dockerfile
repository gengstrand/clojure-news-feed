FROM java:8-jre
RUN \
  apt-get update && \
  mkdir /usr/app
WORKDIR /usr/app
COPY config.yml /usr/app/
COPY example.keystore /usr/app/
COPY newsfeed-dropwizard-1.0.0-SNAPSHOT.jar /usr/app/
EXPOSE 8080
CMD ["java", "-jar", "/usr/app/newsfeed-dropwizard-1.0.0-SNAPSHOT.jar", "server", "/usr/app/config.yml"]

