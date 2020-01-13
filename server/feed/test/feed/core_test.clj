(ns feed.core-test
  (:use clojure.test
        feed.core))

(deftest participant-test
  (testing "testing participant"
    (is (= (to-client (feed.core.Participant. 1 "test")) "{\"id\": 1, \"name\": \"test\", \"link\": \"/participant/1\" }"))))

(deftest friend-test
  (testing "testing friend"
    (is (= (to-cache (feed.core.Friend. 1 2 3)) "(feed.core.Friend. 1 2 3)"))))

(deftest inbound-test
  (testing "testing inbound"
    (is (= (to-cache (feed.core.Inbound. 1 2 "2015-05-01" "test" "test")) "(feed.core.Inbound. /participant/1 /participant/2 \"2015-05-01\" \"test\" \"test\")"))))

(deftest outbound-test
  (testing "testing outbound"
    (is (= (to-client (feed.core.Outbound. 1 "2015-05-01" "test" "test")) "{\"from\": \"/participant/1\", \"occurred\": \"2015-05-01\", \"subject\": \"test\", \"story\": \"test\" }"))))
