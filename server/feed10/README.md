# graphql version of news feed 

This time, I am exploring [GraphQL](https://graphql.org/) by implementing the news feed on [graphql-yoga](https://github.com/prisma/graphql-yoga) which allows you to write your code in typescript which runs on node.js

## Status

This microservice is feature complete and passing the functional tests. The performance test is not so great. Average per minute throughput is 6698. Average latency is 6 ms. Mean latency is 3 ms, 95th percentile is 26 ms and 99th percentile is 44 ms. Basically, latency kept climbing and throughput kept falling lineraly throughout the entire 1.5 hour test run.

## Initial Setup

```bash
npm install
```

## Debugging

```bash
npm run dev
```

## Running the service

How to run the service for dev purposes.

### Locally

You will need to change the configuration to point to local data stores.

This includes the contents of the ormconfig.json file and the following environment variables; REDIS_HOST, NOSQL_HOST, SEARCH_HOST.

```bash
npm run start
```

### Kubernetes

Be sure to edit the deployment manifest to reference your locally built copy.

```bash
docker build -t feed10:1.0 . 
cd ../k8s
kubectl create -f feed10-deployment.yaml
```

## Testing the service

```bash
curl \
  -X POST \
  -H "Content-Type: application/json" \
  --data '{ "query": "mutation { createParticipant(input: {name: \"k8s\"}) { id } }"}' \
  $FEED_URL

curl \
  -X POST \
  -H "Content-Type: application/json" \
  --data '{ "query": "mutation { createParticipant(input: {name: \"graphql\"}) { id } }"}' \
  $FEED_URL

curl \
  -X POST \
  -H "Content-Type: application/json" \
  --data '{ "query": "mutation { createFriend(input: {from_id: 1, to_id: 2}) { to { id } } }"}' \
  $FEED_URL

curl \
  -X POST \
  -H "Content-Type: application/json" \
  --data '{ "query": "mutation { createOutbound(input: {from_id: 1, occurred: \"2019-09-12\", subject: \"test subject\", story: \"test story\"}) { from { id } } }" }' \
  $FEED_URL

curl \
  -X POST \
  -H "Content-Type: application/json" \
  --data '{ "query": "query { participant(id: 2) { inbound { subject } } }"}' \
  $FEED_URL

curl \
  -X POST \
  -H "Content-Type: application/json" \
  --data '{ "query": "query { posters(keywords: \"test\") { name } }"}' \
  $FEED_URL
  
```

## Contributing

The GraphQL boilerplates are maintained by the GraphQL community, with official support from the [Apollo](https://dev-blog.apollodata.com) & [Graphcool](https://blog.graph.cool/) teams.

