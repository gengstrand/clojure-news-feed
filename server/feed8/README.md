# Introduction

News Feed microservice in Spring Boot 

## Overview  

It connects to MySql, Redis, and Cassandra via Spring Data repositories. I was not able to use the Spring Data repository for ElasticSearch because I wanted to use the high level REST client instead of the native transport client. At the time of this writing, that repository did not work with the high level REST client. The elasticsearch folks recommend the high level rest client over the native transport client which will soon be deprecated.

I started this service with the generated output from the [swagger-codegen](https://github.com/swagger-api/swagger-codegen) project using a custom [spring boot template](https://github.com/gengstrand/clojure-news-feed/tree/master/server/swagger/templates/springboot).

The underlying library integrating swagger to SpringBoot is [springfox](https://github.com/springfox/springfox). You can view the api documentation in swagger-ui by pointing to the document root (port 8080).

## Running this service

```
mvn clean install
docker build -t feed8:1.0 .
cd ../k8s
kubectl create -f feed8-deployment.yaml
```

See the [Kubernetes](https://github.com/gengstrand/clojure-news-feed/tree/master/server/k8s) README from this repo on how to set up the rest of the environment. If you make any changes to the code, then be sure to edit the k8s/feed8-deployment.yaml file to launch your new image.

## Performance under load

I ran this service under the usual load test on GKE which experienced an average throughput of 16,913 outbound post RPM. Average latency was 3.5 ms. Median latency was 2 ms, 95th percentile was 11 ms and 99th percentile was 17 ms.

