FROM java:8-jre
RUN \
  apt-get update && \
  mkdir /usr/app
WORKDIR /usr/app
COPY target/universal/news-feed-play-1.0-SNAPSHOT /usr/app/
EXPOSE 8080
CMD ["bin/news-feed-play", "-Dhttp.port=8080"]

