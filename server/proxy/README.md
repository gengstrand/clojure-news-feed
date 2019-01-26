# custom proxy

This is a very simple go service that proxies
all requests to the feed service and logs
all performance information to the
kong logger service.

## building the proxy

```
docker build -t proxy:1.0 .
```

Be sure to edit k8s/proxy-deployment.yaml
to use your image.
