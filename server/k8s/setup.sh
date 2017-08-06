kubectl create -f cassandra-service.yaml
kubectl create -f redis-service.yaml
kubectl create -f mysql-service.yaml
kubectl create -f elasticsearch-service.yaml
kubectl create -f kafka-service.yaml
sleep 10
kubectl create -f cassandra-deployment.yaml
kubectl create -f redis-deployment.yaml
kubectl create -f mysql-deployment.yaml
kubectl create -f elasticsearch-deployment.yaml
kubectl create -f kafka-deployment.yaml
sleep 30
kubectl get services | grep -v NAME | awk -f hosts.awk >../aws/build/hosts.py
python ../aws/build/config.py >../feed3/etc/config.yml


