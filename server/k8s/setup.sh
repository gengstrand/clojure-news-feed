kubectl create -f https://github.com/kubernetes/minikube/blob/master/deploy/addons/kube-dns/kube-dns-svc.yaml
kubectl create -f cassandra-service.yaml
kubectl create -f redis-service.yaml
kubectl create -f mysql-service.yaml
kubectl create -f elasticsearch-service.yaml
sleep 10
kubectl create -f https://github.com/kubernetes/minikube/blob/master/deploy/addons/kube-dns/kube-dns-controller.yaml
kubectl create -f https://github.com/kubernetes/minikube/blob/master/deploy/addons/kube-dns/kube-dns-cm.yaml
kubectl create -f cassandra-deployment.yaml
kubectl create -f redis-deployment.yaml
kubectl create -f mysql-deployment.yaml
kubectl create -f elasticsearch-deployment.yaml
sleep 30
kubectl get services | grep -v NAME | awk -f hosts.awk >../aws/build/hosts.py
python ../aws/build/feed-deployment.py >../k8s/feed-deployment.yaml


