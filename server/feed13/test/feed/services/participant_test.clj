(ns feed.services.participant-test
  (:use clojure.test)
  (:require [mockery.core :refer [with-mock with-mocks]]
  	    [feed.services.participant :as p]))

(deftest get-participant-test
  (testing "testing get participant"
    (with-mock _
      {:target :feed.daos.cache/get-entity
       :return {:id 1 :name "test"}}
      (is (= (:name (p/fetch 1)) "test")))))

(deftest create-participant-test
  (testing "testing create participant"
    (with-mock _
      {:target :feed.daos.participant/create
       :return {:id 1 :name "test"}}
      (is (= (:name (p/create "test")) "test")))))
