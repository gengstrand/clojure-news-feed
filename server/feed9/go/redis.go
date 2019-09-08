package newsfeedserver

import (
	"os"
        "fmt"
	"time"
	"github.com/go-redis/redis"
)

func GetCache() (*redis.Client) {
	cacheHost := fmt.Sprintf("%s:6379", os.Getenv("CACHE_HOST"))
	retVal := redis.NewClient(&redis.Options{
	     Addr: cacheHost,
	     Password: "",
	     DB: 0,
	})
	return retVal
}

var cache = GetCache()

type RedisWrapper struct {
     Cache *redis.Client
}

type CacheWrapper interface {
     Get(key string) (string, error)
     Set(key string, value string, ttl time.Duration)
     Close()
}

func (rw RedisWrapper) Get (key string) (string, error) {
     return rw.Cache.Get(key).Result()
}

func (rw RedisWrapper) Set (key string, value string, ttl time.Duration) {
     rw.Cache.Set(key, value, ttl)
}

func (rw RedisWrapper) Close() {
     rw.Cache.Close()
}

func connectRedis()(RedisWrapper) {
     retVal := RedisWrapper {
     	   Cache: cache, 
     }
     return retVal
}

