# edge proxy for web clients

This service is intended to bridge the gap between the RESTful
news feed microservice and what web clients expect. This go
application bundles oauth2, web sockets, and graphql access
to the underlying news feed microservice.

This service needs to be running before you can run the
client/react application. It assumes that the landing page
for the react application is http://127.0.0.1:8080/

## Deving

Here is how to build this service and run it in Kubernetes.

```bash
docker build -t edge:1.0 .
cd ../../server/k8s
kubectl create -f edge-service.yaml
kubectl create -f edge-deployment.yaml
```

