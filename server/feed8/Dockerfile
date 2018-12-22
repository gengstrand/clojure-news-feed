FROM java:8-jre
RUN \
  apt-get update && \
  mkdir /usr/app
WORKDIR /usr/app
COPY target/newsfeed-springboot-1.0.0-SNAPSHOT.jar /usr/app/
EXPOSE 8080
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/usr/app/newsfeed-springboot-1.0.0-SNAPSHOT.jar"]

