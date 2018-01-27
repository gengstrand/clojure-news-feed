kubectl create -f cassandra-service.yaml
kubectl create -f redis-service.yaml
kubectl create -f mysql-service.yaml
kubectl create -f elasticsearch-service.yaml
sleep 10
kubectl create -f cassandra-deployment.yaml
kubectl create -f redis-deployment.yaml
kubectl create -f mysql-deployment.yaml
kubectl create -f elasticsearch-deployment.yaml
sleep 30
if [ "$#" -ge "1" ]
then
    kubectl get services | grep -v NAME | awk -f hosts.awk >../aws/build/hosts.py
else
    cp hosts.py ../aws/build/hosts.py
fi
python ../aws/build/feed-deployment.py >../k8s/feed-deployment.yaml
python ../aws/build/config.py >../feed3/etc/config.yml
python ../aws/build/configclj.py >../feed/etc/config.clj
python ../aws/build/config-k8s.py >../feed5/swagger_server/config-k8s.cfg
python ../aws/build/settings.properties.py >../feed2/etc/settings.properties



