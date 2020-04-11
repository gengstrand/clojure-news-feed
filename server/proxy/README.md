# custom proxy

This is a very simple golang service that proxies all requests to the feed service and logs
all performance information for those requests to the [kong logger service](https://github.com/gengstrand/clojure-news-feed/tree/master/client/perf4).

It supports all versions of the feed service including non-RESTful, RESTful, and GraphQL versions.

It is instrumented to provide prometheus with performance data too.

## building the proxy

```
docker build -t proxy:1.0 .
```

Be sure to edit k8s/proxy-deployment.yaml
to use your image.
