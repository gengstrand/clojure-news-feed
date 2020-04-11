## kong-logger-service

This micro-service replaces logstash. It is intended to be called by either the
[kong](https://konghq.com/kong-community-edition) plugin [http-log](https://docs.konghq.com/plugins/http-log) or the custom [API gateway](https://github.com/gengstrand/clojure-news-feed/tree/master/server/proxy) in order to record access log data in [Elasticsearch](https://www.elastic.com) and [Kibana](https://www.elastic.co/products/kibana).

See doc/intro.md for more information
