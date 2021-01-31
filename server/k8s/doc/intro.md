# running the test lab on local Kubernetes

You most probably don't want to provision a Kubernetes cluster in public cloud every time you want to do some dev work. This topic shows you how to work with three different implementations of local dev Kubernetes; minikube, microk8s, and kind.

## minikube

I started using [minikube](https://github.com/kubernetes/minikube) on a Linux laptop. The commands to start up minikube may be different for you.

```shell
minikube start --vm-driver=kvm2
eval $(minikube docker-env)
```
If you get an error like this...

Requested operation is not valid: network 'minikube-net' is not active

...then try this...

```shell
virsh
net-start minikube-net
```

### setting up the dependencies

I have written some scripts and configuration files to get the dependent services running. 

#### starting up the cluster

The setup script should work on any Kubernetes cluster. Alternatively, you can provide a use_cluster_ip parameter if you do not want to use the kube-dns cabality. You have to run this scipt only once (unless you delete services and deployments). 

```shell
cd clojure-news-feed/server/k8s
./setup.sh
```

#### initializing the dependent services

You have two alternatives here. You can either run a kubernetes job or a minikube specific script. Here is the kubernetes job. You need to perform this step every time you start up minikube.

```shell
kubectl create -f init-cluster.yaml
```

Here is the minikube specific script approach. You will need to have installed minikube, kubectl, mysql and cqlsh and that they are available in your $PATH environment. You may need to wait a few minutes after starting minikube before you run that initMinikube.sh script.

```shell
./initMinikube.sh
```

### running the service

Note that the feed-deployment.yaml file gets overwritten by the setup.sh script and deploys what we just built. The feed-deployment.yaml file deploys feed4.

This is how you get the URL for the feed service.

```shell
FEED_URL=$(minikube service feed --url)
```

At some point, minikube became too unstable to use reliably anymore. I could not even get all the pods started before it became unresponsive.

## microk8s

I then replaced minikube with [microk8s](https://microk8s.io/)

It is very easy to install on Ubuntu.

```shell
sudo snap install microk8s --classic --channel=1.16/stable
```

This installs a command line tool called microk8s.kubectl which you use in place of kubectl.

```shell
microk8s.start
microk8s.kubectl cluster-info
microk8s.stop
```

This is how you load images built in your local docker repository.

```shell
docker save feed4:1.0 >feed.tar
microk8s.ctr image import feed.tar
```
In order to get the FEED_URL, just run microk8s.kubectl get service feed and use the endpoint address with the http scheme and port 8080.

One day, my entire Ubuntu laptop became unstable. Shortly after booting, it would get slower and slower until the desktop just froze and had to be cold started. The problem turned out to be microk8s so I had to uninstall that.

## kind

After that, I switched to [Kubernetes in Docker](https://kind.sigs.k8s.io/)

After exporting that KUBECONFIG environment variable, you use your normal kubectl command.

```shell
sudo service docker start
kind create cluster
export KUBECONFIG="$(kind get kubeconfig-path)"
kubectl cluster-info
kind load docker-image feed10:1.0 
kind delete cluster
sudo service docker stop
```

You have to use port forwarding to access the microservice.

```shell
kubectl port-forward deployment/feed 8080:8080
FEED_URL=http://127.0.0.1:8080
```

# Setting Up Grafana

You need to add two more pods to your test lab.

```bash
kubectl create -f prometheus.yaml
kubectl create -f grafana.yaml
# wait a bit
kubectl get svc grafana
```

Point your browser to port 3000 of the external IP that gets created. Go to the settings and connect to the prometheus service on port 9090. Create a dashboard. Here are some latency based queries for you to use in various panels for that dashboard.

## average latency (seconds)

rate(outbound_POST_200_sum[5m]) / rate(outbound_POST_200_count[5m])

rate(participant_POST_200_sum[5m]) / rate(participant_POST_200_count[5m])

rate(friends_POST_200_sum[5m]) / rate(friends_POST_200_count[5m])

## 95th percentile latency (seconds)

histogram_quantile(0.95, sum(rate(outbound_POST_200_bucket[5m])) by (le))

histogram_quantile(0.95, sum(rate(participant_POST_200_bucket[5m])) by (le))

histogram_quantile(0.95, sum(rate(friends_POST_200_bucket[5m])) by (le))

## 99th percentile latency (seconds)

histogram_quantile(0.99, sum(rate(outbound_POST_200_bucket[5m])) by (le))

histogram_quantile(0.99, sum(rate(participant_POST_200_bucket[5m])) by (le))

histogram_quantile(0.99, sum(rate(friends_POST_200_bucket[5m])) by (le))

## median latency (seconds)

histogram_quantile(0.50, sum(rate(outbound_POST_200_bucket[5m])) by (le))

histogram_quantile(0.50, sum(rate(participant_POST_200_bucket[5m])) by (le))

histogram_quantile(0.50, sum(rate(friends_POST_200_bucket[5m])) by (le))
