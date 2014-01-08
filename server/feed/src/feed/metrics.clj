(ns feed.metrics)

(def participant-mbean (com.dynamicalsoftware.support.FeedPerformanceMBeanImpl. 1000 60000))
(def friends-mbean (com.dynamicalsoftware.support.FeedPerformanceMBeanImpl. 1000 60000))
(def inbound-mbean (com.dynamicalsoftware.support.FeedPerformanceMBeanImpl. 1000 60000))
(def outbound-mbean (com.dynamicalsoftware.support.FeedPerformanceMBeanImpl. 1000 60000))

(com.dynamicalsoftware.support.FeedPerformanceMBeanImpl/register "clojure.feed:name=participant,type=performance" participant-mbean)
(com.dynamicalsoftware.support.FeedPerformanceMBeanImpl/register "clojure.feed:name=friends,type=performance" friends-mbean)
(com.dynamicalsoftware.support.FeedPerformanceMBeanImpl/register "clojure.feed:name=inbound,type=performance" inbound-mbean)
(com.dynamicalsoftware.support.FeedPerformanceMBeanImpl/register "clojure.feed:name=outbound,type=performance" outbound-mbean)


