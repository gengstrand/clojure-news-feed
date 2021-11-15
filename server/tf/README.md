# provisioning a Kubernetes cluster via terraform

See this blog on [Devops Renaissance](https://glennengstrand.info/software/architecture/devops/renaissance) for more information.

## requirements

1. kubectl
2. helm
3. terraform

Go to the cloud vendor specific sub folder and follow the README instructions there.

Once, that has completed, here are the commands to launch the load test.

```bash
cd ../../helm
vi values.yaml # loadTest: true
helm install feed .
kubectl get pods # repeat until ready
cd ../k8s
kubectl create -f load_test.yaml
kubectl get pods # repeat until ready
```

How to monitor the load test results. You need a little port forwarding before you can access Kibana or Grafana in your web browser.

```bash
kubectl port-forward deployment/kibana-logger 5601:5601 &
kubectl port-forward deployment/grafana 3000:3000 &
```

How to decommision the load test.

```bash
ps -ef | grep port-forward | grep -v grep | awk '{ printf "kill %d\n", $2}' | sh
kubectl delete -f load_test.yaml
cd ../helm
helm uninstall feed
```