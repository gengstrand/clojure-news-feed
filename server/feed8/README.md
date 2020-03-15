# Introduction

News Feed microservice in [Spring Boot](https://spring.io/projects/spring-boot). I evaluated this microservice when compared to the Dropwizard version.

https://glennengstrand.info/software/performance/springboot/dropwizard

## Overview  

It connects to MySql, Redis, and Cassandra via [Spring Data](https://spring.io/projects/spring-data) repositories. I was not able to use the Spring Data repository for ElasticSearch because I wanted to use the high level REST client instead of the native transport client. At the time of this writing, that repository did not work with the high level REST client. The ElasticSearch folks recommend the high level rest client over the native transport client which will soon be deprecated.

I started this service with the generated output from the [swagger-codegen](https://github.com/swagger-api/swagger-codegen) project using a custom [spring boot template](https://github.com/gengstrand/clojure-news-feed/tree/master/server/swagger/templates/springboot).

The underlying library integrating swagger to SpringBoot is [springfox](https://github.com/springfox/springfox). You can view the api documentation in swagger-ui by pointing to the document root (port 8080).

## Running this service

```
mvn clean install
docker build -t feed8:1.0 .
cd ../k8s
kubectl create -f feed8-deployment.yaml
```

See the [Kubernetes README](https://github.com/gengstrand/clojure-news-feed/tree/master/server/k8s) from this repo on how to set up the rest of the environment. If you make any changes to the code, then be sure to edit the k8s/feed8-deployment.yaml file to launch your new image.

## Performance under load

With Spring boot you can switch out the underlying servlet engine. Here are the performance benchmarks for Tomcat and Jetty.

| servlet engine | throughput (RPM) | avg latency (ms) | 95th (ms) | 99th (ms) | RAM (MB) |
|----------------|------------------|------------------|-----------|-----------|----------|
| Tomcat         | 13,966           | 3                | 5         | 9         | 800      |
| Jetty          | 13,895           | 2.76             | 4         | 7         | 1,536    |
 
The above benchmark numbers were run with the default Hikari connection pool size of 10. I ran the load test with a pool size of 18 (because that is the pool size for the [dropwizard version](https://github.com/gengstrand/clojure-news-feed/tree/master/server/feed3) of the news feed microservice) but the throughput was one third of what you see here and the latency was double.


