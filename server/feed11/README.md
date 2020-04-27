# News feed microservice in Scala on Vert.x

This version of the news feed service is written in Scala using the [Vert.x framework](https://vertx.io/blog/scala-is-here/). Another big difference between this implementation and the previous implementations is that this is designed in a style known as reactive programming.

## Building

This is a typical scala project using the scala built tool.

```bash
sbt
compile
test
```

## Deving

You can run this locally but be sure to set the environment variables to indicate
where the dependent data stores are. You will also need to edit the src/main/resources/application.conf file to use 127.0.0.1 instead of mysql for the mysql host.

```bash
kubectl port-forward deployment/cassandra 9042:9042 &
export NOSQL_HOST=127.0.0.1
kubectl port-forward deployment/mysql 3306:3306 &
export MYSQL_HOST=127.0.0.1
kubectl port-forward deployment/redis 6379:6379 &
export CACHE_HOST=127.0.0.1
kubectl port-forward deployment/elasticsearch 9200:9200 & 
export SEARCH_HOST=127.0.0.1
sbt
> console
scala> vertx.deployVerticle(nameForVerticle[info.glennengstrand.news.HttpVerticle])
```

From here you can freely interact with the Vert.x API inside the sbt-scala-shell. You can also curl http://127.0.0.1:8080 for testing the service itself.

## Building the Uber Jar

To create the runnable uber jar use:

```bash
sbt assembly
```

## Building the Docker Image

The project also contains everything you need to build the Docker container. Simply run the following commands to package your uber jar inside a Docker container

```bash
sbt docker
docker tag default/feed11:latest feed11:1.0
```

The k8s/feed11-deployment.yaml contains the manifest for running this image. You will need to edit this file in order to run what you just built.


