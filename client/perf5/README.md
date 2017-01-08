# perf5

monitor search appliance statistics

## Installation

lein uberjar

## Usage

query the configured search service for statistics 
and output the relevant metrics to the command line in CSV format

    $ java -jar perf5-0.1.0-standalone.jar type host port index times interval

## Options

||name||description||
|type|either solr or elasticsearch|
|host|the ip address of the search appliance|
|port|most likely either 8983 or 9200|
|index|the name of the index or core whose stats are to be monitored|
|times|the number of times to query for stats|
|interval|the number of milliseconds to wait between each query|

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
