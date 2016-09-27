FROM java7
RUN \
  apt-get update && \
  mkdir /usr/app
WORKDIR /usr/app
COPY config.yml /usr/app/
COPY news-feed-assembly-0.1.0-SNAPSHOT.jar /usr/app/
EXPOSE 8080
CMD ["java", "-jar", "/home/ec2-user/newsfeed-dropwizard-1.0.0-SNAPSHOT.jar", "server", "/usr/app/config.yml"]

