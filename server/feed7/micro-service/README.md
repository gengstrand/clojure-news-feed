# news feed micro-service in javascript

## Overview

This is yet another implementation of the rudimentary news feed micro-service only this time implemented in node.js and integrating with Hyperledger Composer instead of MySql, Redis, and Cassandra.

### Running the unit tests

```
sudo yum install nodejs npm --enablerepo=epel
npm install
npm test
```

### Running the server

```
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

