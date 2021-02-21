# news feed server built with clojure on donkey

[Donkey](https://github.com/AppsFlyer/donkey) is an HTTP server that uses Vert.x

Here are the list of dependencies that I am considering.

library | group | artifact | version | url
------- | ----- | -------- | ------- | ---
mysql | org.jdbi | jdbi3-core | 3.16.0 | [Jdbi 3 Developer Guide](https://jdbi.org)
mysql | mysql | mysql-connector-java | 8.0.23 | [MySql Connector/J](https://dev.mysql.com/doc/connector-j/8.0/en/)
redis | redis.clients | jedis | 3.5.1 | [repo](https://github.com/redis/jedis)
cassandra | com.datastax.oss | java-driver-core | 4.10.0 | 4.10.0 | [repo](https://github.com/datastax/java-driver)
cassandra | com.datastax.oss | java-driver-query-builder | 4.10.0 | [repo](https://github.com/datastax/java-driver)
cassandra | com.datastax.oss | java-driver-mapper-runtime | 4.10.0 | [repo](https://github.com/datastax/java-driver)
elasticsearch | org.elasticsearch.client | elasticsearch-rest-high-level-client | 7.11.1 | [Java High Level REST Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html)
json | org.clojure | data.json | 1.0.0 | [repo](https://github.com/clojure/data.json)

