module feed9

go 1.15

require (
	github.com/fortytw2/leaktest v1.3.0 // indirect
	github.com/go-redis/redis v6.15.9+incompatible
	github.com/onsi/ginkgo v1.16.5 // indirect
	github.com/onsi/gomega v1.27.2 // indirect
	newsfeedserver v0.0.0-00010101000000-000000000000
)

replace newsfeedserver => ./go
