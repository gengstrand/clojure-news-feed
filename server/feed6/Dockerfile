FROM java:8-jre
RUN \
  apt-get update && \
  mkdir /usr/app
WORKDIR /usr/app
COPY target/scala-2.12/scalatra-news-feed-assembly-0.1.0-SNAPSHOT.jar /usr/app/
ENV MYSQL_USER feed
ENV MYSQL_PASSWORD feed1234
EXPOSE 8080
CMD ["java", "-jar", "/usr/app/scalatra-news-feed-assembly-0.1.0-SNAPSHOT.jar"]

