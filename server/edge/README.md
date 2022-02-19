# edge proxy for front end clients

This service is intended to bridge the gap between the RESTful news feed microservice and what web clients expect. This go application bundles oauth2, web sockets, and graphql access to the underlying news feed microservice.

This service needs to be running before you can run the client/react application. It is tightly bundled with the client/react web app and it currently configured for deving that SPA locally with the landing page being http://127.0.0.1:3000/ but you can reconfigure it to dev the client react web app in Kubernetes by changing that port to 8080.

## Deving

Here is how to build this service and run it in Kubernetes. I currently dev using [Kubernetes in Docker](https://kind.sigs.k8s.io/) which explains the kind command. You won't need that command if you are using something else.

```bash
docker build -t edge:1.0 .
kind load docker-image edge:1.0
cd ../../server/k8s
kubectl create -f edge-service.yaml
kubectl create -f edge-deployment.yaml
```

## Learn More

This orchestration service is actually a mash up of some fairly obscure golang projects, [oauth2](https://github.com/go-oauth2/oauth2/), [websocket](https://github.com/gorilla/websocket), and [graphql](https://github.com/graphql-go/graphql) are the repos that this service depends on.