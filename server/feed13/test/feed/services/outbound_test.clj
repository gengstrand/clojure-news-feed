(ns feed.services.outbound-test
  (:use clojure.test)
  (:require [mockery.core :refer [with-mock with-mocks]]
            [feed.services.outbound :as o]))

(deftest get-outbound-test
  (testing "testing get outbound"
    (with-mock _
      {:target :feed.daos.outbound/fetch
       :return [{:from 1 :occurred "2021-02-18" :subject "test" :story "test story"}]}
      (is (= (:subject (first (o/fetch 1))) "test")))))

(deftest create-outbound-test
  (testing "testing create outbound"
    (with-mocks
      [dao {:target :feed.daos.outbound/create
       	    :return {:from 1 :occurred "2021-02-18" :subject "test" :story "test story"}}
       fri-svc {:target :feed.services.friends/fetch
                :return [{:id 1 :from 1 :to 2}]}
       inb-svc {:target :feed.services.inbound/create
       	        :return {:from 1 :to 2 :occurred "2021-02-18" :subject "test" :story "test story"}}
       es-dao {:target :feed.daos.search/index
       	       :return []}]
      (is (= (:subject (o/create 1 "2021-02-18" "test" "test story")) "test")))))
