# news feed microservice in JavaScript

## Overview

This is yet another implementation of the rudimentary news feed microservice only this time implemented in Node.js

You can learn more about this project by reading the following blogs; [Node.js vs DropWizard](http://glennengstrand.info/software/performance/nodejs/dropwizard) and [Python Flask vs Node.js](http://glennengstrand.info/software/performance/nodejs/python), and [GraphQL vs REST](http://glennengstrand.info/software/architecture/microservice/graphql).

### Running the unit tests

```
sudo yum install nodejs npm --enablerepo=epel
npm install
npm test
```

### Running the server

```
export MYSQL_HOST="127.0.0.1"
export MYSQL_USER="feed"
export MYSQL_PASS="feed"
export MYSQL_DB="feed"
export NOSQL_HOST="127.0.0.1"
export REDIS_HOST="127.0.0.1"
export SEARCH_HOST="127.0.0.1"
export SEARCH_PATH="/feed/stories"
npm start
```

Be advised that, at the time of this writing, I was not able to re ssh back in to the EC2 instance once I have installed node there. 

To view the Swagger UI interface:

```
open http://localhost:8080/docs

```

## Running the Docker image

```bash
sudo yum update -y
sudo yum install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user
sudo docker build -t feed .
sudo docker run -d --net=host --env-file ./env.list feed
```

## Dependencies

This project uses [swagger-tools](https://github.com/apigee-127/swagger-tools) which parses the swagger model at launch time to generate the RESTful interface programmatically.

This example uses the [expressjs](http://expressjs.com/) framework.  To see how to make this your own, look here:

[README](https://github.com/swagger-api/swagger-codegen/blob/master/README.md)

You can find the swagger assets used to generate the RESTful parts of this project [here](https://github.com/gengstrand/clojure-news-feed/tree/master/server/swagger).

Here is the technology stack.

[mysql](https://www.npmjs.com/package/mysql) is the relational database for participant and friend data.

[redis](https://github.com/NodeRedis/node_redis) is the external cache that fronts mysql.

[cassandra](https://github.com/datastax/nodejs-driver) is the nosql database for inbound and outbound feed data.

This technology is used in the unit tests.

[mocha](http://mochajs.org/) is the testing framework.

[chai](http://chaijs.com/) is the assertion library.

[mockery](https://github.com/mfncooper/mockery) is used to mock the repository code.

## Load Testing

I ran my standard load test (see client/load) in the Kubernetes test lab (see server/k8s) for two hours. The average per minute throughput of output posts was 12,629 with an average duration of 5 ms, a median of 5 ms, and a 99th percentile of 12 ms. 