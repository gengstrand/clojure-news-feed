# graphql version of news feed 

This time, I am exploring [GraphQL](https://graphql.org/) by implementing the news feed on [graphql-yoga](https://github.com/prisma/graphql-yoga) which allows you to write your code in typescript which runs on node.js

## Status

I am just starting this microservice so there is a long way to go before it acutally works.

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

```bash
npm run start
```

### Kubernetes

```bash
docker build -t feed10:1.0
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
  
```

## Contributing

The GraphQL boilerplates are maintained by the GraphQL community, with official support from the [Apollo](https://dev-blog.apollodata.com) & [Graphcool](https://blog.graph.cool/) teams.

