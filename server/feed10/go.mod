module github.com/gengstrad/clojure-news-feed/server/feed10

go 1.13

require (
	github.com/alecthomas/template v0.0.0-20190718012654-fb15b899a751 // indirect
	github.com/alecthomas/units v0.0.0-20190910110746-680d30ca3117 // indirect
	github.com/giantswarm/retry-go v0.0.0-20151203102909-d78cea247d5e
	github.com/go-redis/redis v6.15.5+incompatible
	github.com/go-sql-driver/mysql v1.4.1
	github.com/gocql/gocql v0.0.0-20190915153252-16cf9ea1b3e2
	github.com/google/uuid v1.1.1
	github.com/gorilla/mux v1.7.3
	github.com/juju/errgo v0.0.0-20140925100237-08cceb5d0b53 // indirect
	gopkg.in/alecthomas/kingpin.v2 v2.2.6
	gopkg.in/olivere/elastic.v3 v3.0.75
)
