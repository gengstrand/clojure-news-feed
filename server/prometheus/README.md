# prometheus

You can use [prometheus](https://prometheus.io/) to monitor and alert on microservices. I have instrumented the proxy service to integrate with prometheus. This custom docker image is configured to monitor the proxy service.

## building and deploying

```
docker build -t prometheus:1.0 .
cd ../k8s
kubectl create -f prometheus.yaml
```

The k8s/prometheus.yaml deployment manifest currently points to the image on my dockerhib repo.

## querying

Here are some sample queries to get you started.

throughput in requests per second

```
sum(rate(outbound_POST_200_count[5m]))
```

mean latency for outbound post

```
rate(outbound_POST_200_sum[5m]) / rate(outbound_POST_200_count[5m])
```

median latency for outbound post

```
histogram_quantile(0.90, sum(rate(outbound_POST_200_bucket[5m])) by (le))
```

95th percentile for outbound post

```
histogram_quantile(0.95, sum(rate(outbound_POST_200_bucket[5m])) by (le))
```

99th percentile for outbound post

```
histogram_quantile(0.99, sum(rate(outbound_POST_200_bucket[5m])) by (le))
```
