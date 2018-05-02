# load

A command line tool to test the clojure-new-feed feed web service

## Usage

This is how to invoke the load test.

```
lein uberjar

java -jar target/load-0.1.0-SNAPSHOT-standalone.jar feed-host feed-port concurrent-users percent-searches use-json
```
where

argument | meaning
--- | --- 
feed-host | IP address of the service name of the feed microservice or kong gateway
feed-port | port number that the microservice or kong gateway is listening to
concurrent-users | numbmer of threads performing the load test
persent-searches | percentage of requests run the search scenario
use-json | should be true for feed 3, 4, 5, or 6

See the README under k8s for how to invoke the integration test

## License

Copyright © 2014 Glenn Engstrand

Distributed under the Eclipse Public License, the same as Clojure.
