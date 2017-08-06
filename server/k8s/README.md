# running on kubernetes

Introduction on how to run the news feed service in kubernetes.

## minikube

```shell
minikube start --vm-driver=kvm
eval $(minikube docker-env)
```

### setting up the dependencies

```shell
cd clojure-news-feed/server/k8s
./setup.sh
./initMinikube.sh
```

### building the service

```shell
cd ../feed3/etc
mvn package
cp target/newsfeed-dropwizard-1.0.0-SNAPSHOT.jar etc
cd etc
docker build -t feed3:1.0 .
```

### running the service

```shell
cd ../../k8s
kubectl create -f feed-service.yaml
kubectl create -f feed-deployment.yaml
```

