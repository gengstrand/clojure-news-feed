(ns feed.services.friends-test
  (:use clojure.test)
  (:require [mockery.core :refer [with-mock with-mocks]]
            [feed.services.friends :as f]))

(deftest get-friend-test
  (testing "testing get friends"
    (with-mock _
      {:target :feed.daos.cache/get-entity
       :return [{:id 1 :from 1 :to 2}]}
      (is (= (:to (first (f/fetch 1))) 2)))))

(deftest create-friend-test
  (testing "testing create friends"
    (with-mock _
      {:target :feed.daos.friends/create
       :return {:id 1 :from 1 :to 2}}
      (is (= (:to (f/create 1 2)) 2)))))
