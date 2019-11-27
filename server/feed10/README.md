# graphql version of news feed 

This time, I am exploring [GraphQL](https://graphql.org/) by implementing the news feed on [graphql-yoga](https://github.com/prisma/graphql-yoga) which allows you to write your code in typescript which runs on node.js

I blogged about this here.

http://glennengstrand.info/software/architecture/microservice/graphql

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

## Load Test Results

Average per minute throughput of outbound posts is 9,817. Mean latency is 5 ms. Median latency is 4 ms, 95th percentile is 8 ms and 99th percentile is 15 ms. This excerpt from the profile suggests that the graphql components do have some overhead.

```
 [Bottom up (heavy) profile]:
  Note: percentage shows a share of a particular caller in the total
  amount of its parent calls.
  Callers occupying less than 1.0% are not shown.

   ticks parent  name
  15943    3.0%  LoadIC: A load IC from the snapshot
   1144    7.2%    LazyCompile: *leave /usr/app/node_modules/graphql/language/visitor.js:337:26
    773   67.6%      LazyCompile: *leave /usr/app/node_modules/graphql/language/visitor.js:388:26
    773  100.0%        LazyCompile: *visit /usr/app/node_modules/graphql/language/visitor.js:154:15
    765   99.0%          LazyCompile: *validate /usr/app/node_modules/graphql/validation/validate.js:48:18
    765  100.0%            LazyCompile: *doRunQuery /usr/app/node_modules/apollo-server-core/dist/runQuery.js:44:20
      8    1.0%          LazyCompile: ~validate /usr/app/node_modules/graphql/validation/validate.js:48:18
      7   87.5%            LazyCompile: *doRunQuery /usr/app/node_modules/apollo-server-core/dist/runQuery.js:44:20
      1   12.5%            LazyCompile: ~doRunQuery /usr/app/node_modules/apollo-server-core/dist/runQuery.js:44:20
```

## Contributing

The GraphQL boilerplates are maintained by the GraphQL community, with official support from the [Apollo](https://dev-blog.apollodata.com) & [Graphcool](https://blog.graph.cool/) teams.

