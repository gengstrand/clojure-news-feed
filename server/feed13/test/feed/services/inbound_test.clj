(ns feed.services.inbound-test
  (:use clojure.test)
  (:require [mockery.core :refer [with-mock with-mocks]]
            [feed.services.inbound :as i]))

(deftest get-inbound-test
  (testing "testing get inbound"
    (with-mock _
      {:target :feed.daos.inbound/fetch
       :return [{:from 1 :to 2 :occurred "2021-02-18" :subject "test" :story "test story"}]}
      (is (= (:subject (first (i/fetch 1))) "test")))))

(deftest create-inbound-test
  (testing "testing create inbound"
    (with-mock _
      {:target :feed.daos.inbound/create
       :return {:from 1 :to 2 :occurred "2021-02-18" :subject "test" :story "test story"}}
      (is (= (:subject (i/create 1 2 "2021-02-18" "test" "test story")) "test")))))
