# running on Kubernetes

Introduction on how to run the news feed service in Kubernetes.

<img src="components.png" />

## running on local Kubernetes

Learn how to run Kubernetes locally using either minikube, microk8s, or kind [here](https://github.com/gengstrand/clojure-news-feed/blob/master/server/k8s/doc/intro.md)

### building the service

The existing deployment manifests load prebuilt images from my dockerhub account but you can edit those locally to use your own builds. Build a docker image for the feed service that you wish to deploy. Here is how to do that for the node.js (feed4) version. See the README information in the project folder for instructions on how to do that.

```shell
cd ../feed4
docker build -t feed4:1.0 .
```

### running the service

There are other feed deployment manifests in this folder for deploying any version of the feed service that you are interested in but you have to edit them if you want to run what you built locally. See the kubectl commands near the bottom of this topic for launching and initializing Redis, MySql, Cassandra, and Elasticsearch.

```shell
cd ../k8s
kubectl create -f configmap.yaml
kubectl create -f feed-service.yaml
kubectl create -f feed-deployment.yaml
```
### testing the service

This shell script demonstrates how to call feeds 3 - 9 for a basic end-to-end test which includes creating two participants, friending them, posting a news feed item on one friend's outbound feed, querying the other friend's inbound feed, and keyword based search. See [here](https://github.com/gengstrand/clojure-news-feed/blob/master/server/feed/doc/intro.md) for how to test feeds 1 and 2 and [here](https://github.com/gengstrand/clojure-news-feed/blob/master/server/feed10/README.md) for how to test feed 10.

```shell
curl -H "Content-Type: application/json" -d '{"name":"testing dropwizard"}' ${FEED_URL}/participant/new

curl -H "Content-Type: application/json" -d '{"name":"testing minikube"}' ${FEED_URL}/participant/new

curl -H "Content-Type: application/json" -d '{"from":1,"to":2}' ${FEED_URL}/friends/new

curl -H "Content-Type: application/json" -d '{"from":1,"subject":"testing for dropwizard","story":"Kubernetes rocks!"}' ${FEED_URL}/outbound/new

curl ${FEED_URL}/inbound/2

curl -X POST -g "${FEED_URL}/outbound/search?keywords=kubernetes"
```

Alternatively, you can also run the testMinikube.sh to perform the automated version of this test. This script runs the load test app in integration test mode so you will need to have built what is in the client/load project.

## Measuring Performance Under Load

An important part to researching different technologies for the news feed microservice is evaluating
their performance under test load. I used to use Kafka to capture the performance data but Kafka is
not easy to set up in Kubernetes. I switched to a different approach where the load test application
makes all of its calls through a full reverse proxy which also sends performance data to another
microservice which prepares it for ingestion into elasticsearch in a kibana friendly way.

I don't recommend running the load test in local Kubernetes as it will pretty much overwhelm your laptop. You should most probably conduct load tests on a real Kubernetes cluster.

### Optional Kong Integration

I used to use the open source API Gateway software [Kong](https://getkong.org) for this full reverse proxy that captures performance data. With version 1, I started encountering [throughput issues](https://discuss.konghq.com/t/http-log-plugin-not-logging-every-request/2655) so I replaced Kong with a feature identical (for my purposes) custom [proxy](https://github.com/gengstrand/clojure-news-feed/tree/master/server/proxy) written in go.

```shell
kubectl create -f kong_service.yaml
kubectl create -f kong_migration_cassandra.yaml
# run this next line until the kong-migration job is successful
kubectl get jobs
kubectl create -f kong_cassandra.yaml
```

### Using the custom go proxy

This is a drop in replacement that looks just like the Kong proxy.

```shell
kubectl create -f proxy-service.yaml
kubectl create -f proxy-deployment.yaml
```

### Launching the Kong Logger Service and Running the Load Test Job

```shell
cd clojure-news-feed/server/k8s
kubectl create -f kong-logger-service.yaml
kubectl create -f kong-logger-deployment.yaml
kubectl create -f load_test.yaml 
```
Be advised that, if you are testing feed 1 or 2, then you should use the load_test_legacy.yaml instead of the load_test.yaml manifest. After that, you should be able to reach the feed service via Kong this way.

#### Optional Build the Kong-Logger service and the load test job

Currently, the deployment configurations point to these images on my [Docker Hub account](https://hub.docker.com/r/gengstrand) so you don't really need to build these locally. If you wanted to modify what these do, then you will need to build the images and modify the deployment configurations to reference the local docker repository.

```shell
cd clojure-news-feed/client/perf4
mvn package
docker build -t kong-logger:1.0 .
cd ../load
lein uberjar
docker build -t load:1.0 .
```

### Launch Kibana

```shell
kubectl create -f kibana-service.yaml 
kubectl create -f kibana-deployment.yaml
```

If you call the news feed APIs with the kong-proxy URL, then you will be able to track performance by pointing your web browser to the kibana-logger URL.

## Kubernetes in the Cloud

Once you have everything working the way you want on your personal computer, you will most likely want to see how to deploy it to the cloud. Here is a blog on the differences between running Kubernetes on Amazon's and on Google's cloud.

http://glennengstrand.info/software/performance/eks/gke

Here are some tips on how to do that on both GKE and EKS.

### Google Kubernetes Engine

I created a project called feed and a cluster called feed-test using 7 n1-standard-4 instances through Kubernetes Engine part of the the Google Cloud Platform dashboard. Then I ran the following in the gcloud console.

```
gcloud config set project feed-193503
gcloud config set compute/zone us-central1-a
gcloud container clusters get-credentials feed-test --zone us-central1-a --project feed-193503
git clone https://github.com/gengstrand/clojure-news-feed.git
cd clojure-news-feed/server/k8s
```

### Amazon Elastic Container Service for Kubernetes

It is not as easy to provision a Kubernetes cluster on EKS as it is on GKE. If you are new to EKS, then it will take hours to make it through this [getting started](https://docs.aws.amazon.com/eks/latest/userguide/getting-started.html) topic but that is what you are going to have to do. Read that long and detailed topic very carefully and be prepared to follow it to the letter. There are plenty of gotchas here and you need to be very familiar with IAM, CloudFormation, EKS, AWS CLI, and the AWS IAM Authenticator for Kubernetes. You will need to use both the AWS Console and CLI. Some things that they list as optional are actually mandatory.

* Create the EKS Service Role
* Ensure that you have IAM user credentials (with AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY) that is allowed to sts:AssumeRole with the EKS Service Role.
* Create the EKS Cluster VPC
* Install the latest AWS CLI with the IAM user credentials above
* You should create the EKS cluster via the command line.

```
aws eks create-cluster --name devel --role-arn arn:aws:iam::111122223333:role/eks-service-role-AWSServiceRoleForAmazonEKS-EXAMPLEBKZRQR --resources-vpc-config subnetIds=subnet-a9189fe2,subnet-50432629,securityGroupIds=sg-f5c54184
aws eks describe-cluster --name devel --query cluster.status
aws eks describe-cluster --name devel  --query cluster.endpoint --output text
aws eks describe-cluster --name devel  --query cluster.certificateAuthority.data --output text
```

* Install and configure kubectl for EKS. You will need to update the configuration with the cluster endpoint and certificate authority data retrieved above.
* Install aws-iam-authenticator for EKS
* Launch and configure EKS Worker Nodes. I used 7 m4.xlarge instances.
* Enable those worker nodes to join the cluster

```
curl -O https://amazon-eks.s3-us-west-2.amazonaws.com/cloudformation/2018-08-30/aws-auth-cm.yaml
# edit that file as documented
kubectl apply -f aws-auth-cm.yaml
```

### Spinning up the Services

Whether you have provisioned your cluster on the Public Cloud vendor of choice or running a local version of Kubernetes, you can proceed with the cloud vendor neutral Kubernetes commands. 

```
kubectl create -f cassandra-service.yaml
kubectl create -f redis-service.yaml
kubectl create -f mysql-service.yaml
kubectl create -f elasticsearch-service.yaml
kubectl create -f cassandra-deployment.yaml
kubectl create -f redis-deployment.yaml
kubectl create -f mysql-deployment.yaml
kubectl create -f elasticsearch-deployment.yaml
# run this next line until all of the pods are running
kubectl get pods
kubectl create -f init-cluster.yaml
# run this next line until the init-cluster job is successful
kubectl get jobs
kubectl create -f configmap.yaml
kubectl create -f feed-service.yaml
kubectl create -f feed-deployment.yaml
kubectl create -f kong-logger-service.yaml
# I used to use kong as the performance logging proxy but
# version 1 underperformed so I replaced it with a
# custom proxy written in go
# kubectl create -f kong_service.yaml
# kubectl create -f kong_migration_cassandra.yaml
# run this next line until the kong-migration job is successful
# kubectl get jobs
# kubectl create -f kong_cassandra.yaml
kubectl create -f kong-logger-deployment.yaml
kubectl create -f proxy-service.yaml
kubectl create -f proxy-deployment.yaml
kubectl create -f kibana-service.yaml 
kubectl create -f kibana-deployment.yaml
kubectl create -f load_test.yaml
```

Once I collected the data, I cleaned up everything before deleting the cluster.

```
kubectl delete job init-cluster
# kubectl delete job kong-migration
kubectl delete deployment load-test
kubectl delete deployment kibana-logger
kubectl delete deployment kong-logger
kubectl delete deployment kong-proxy
kubectl delete deployment feed
kubectl delete deployment elasticsearch
kubectl delete deployment redis
kubectl delete deployment mysql
kubectl delete deployment cassandra
kubectl delete service load-test
kubectl delete service cassandra
kubectl delete service elasticsearch
kubectl delete service feed
kubectl delete service kibana-logger
kubectl delete service kong-logger
kubectl delete service kong-proxy
kubectl delete service mysql
kubectl delete service redis
```