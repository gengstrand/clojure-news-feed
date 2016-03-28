# load

A command line tool to load test the clojure-new-feed feed web service

## Usage

Locate the host and port global variables in load.core and provide correct values

lein uberjar

java -jar target/load-0.1.0-SNAPSHOT-standalone.jar feed-host concurrent-users percent-searches

## License

Copyright © 2014 Glenn Engstrand

Distributed under the Eclipse Public License, the same as Clojure.
