# etl

A command line tool that inputs the summarized output from the 
clojure-news-feed service as reported by the kafka feed topic
and loads a mysql database for the purposes of mondrian OLAP reporting

## Usage

edit the mysql-db properties in etl.core

lein repl

(process-data-file "path/and/file/to/part-00000" mysql-db)

## License

Copyright © 2014 Glenn Engstrand

Distributed under the Eclipse Public License, the same as Clojure.
