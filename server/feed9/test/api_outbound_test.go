package testnewsfeed

import (
	"time"
        "testing"
	"github.com/go-redis/redis"
	sw "../go"
)

type MockErrorWrapper struct {
     t *testing.T
}

func (lw MockErrorWrapper) LogError(err error, format string, status int) {
     lw.t.Errorf(format, err)
}

var InboundCounter = 0

type MockCassandraWrapper struct {
}

func (cw MockCassandraWrapper) AddInbound(i sw.Inbound) {
     InboundCounter++
}

func (cw MockCassandraWrapper) AddOutbound(o sw.Outbound) {
}

type MockRedisWrapper struct {
     SetCounter int64
}

func (rw MockRedisWrapper) Get(key string) (string, error) {
     return "", redis.Nil
}

func (rw MockRedisWrapper) Set(key string, value string, ttl time.Duration) {
     rw.SetCounter++
}

func (rw MockRedisWrapper) Close() {
}

type MockMySqlWrapper struct {
     Friends []sw.Friend
}

func (mw MockMySqlWrapper) Close() {
}

func (mw MockMySqlWrapper) FetchFriends(id string)([]sw.Friend, error) {
     return mw.Friends, nil
}

func AddFriend(results []sw.Friend, id int64, from int64, to int64) ([]sw.Friend) {
     f := sw.Friend{
	 Id: id,
	 From: sw.ToLink(from),
	 To: sw.ToLink(to),
     }
     results = append(results, f)
     return results
}

func TestAddOutboundInner(t *testing.T) {
     ew := MockErrorWrapper{
     	t: t,
     }
     cw := MockCassandraWrapper{
     }
     rw := MockRedisWrapper{
     	SetCounter: 0,
     }
     ob := sw.Outbound {
     	From: sw.ToLink(1),
	Occurred: time.Now(),
	Subject: "test subject",
	Story: "test story",
     }
     var results []sw.Friend
     results = AddFriend(results, 1, 1, 2)
     results = AddFriend(results, 2, 1, 3)
     results = AddFriend(results, 3, 1, 4)
     mw := MockMySqlWrapper{
     	Friends: results,
     }
     sw.AddOutboundInner(ob, ew, cw, rw, mw)
     if InboundCounter != 3 {
     	t.Errorf("expected 3 inbound but got %d instead", InboundCounter)
     }
}
