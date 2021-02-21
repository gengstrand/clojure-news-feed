(ns feed.daos.inbound)

(defn fetch
  "fetch the inbound news feed items for a participant"
  [id]
  [{:from 1 :to 2 :occurred "2021-02-18" :subject "test" :story "test story"}])

(defn create
  "create an inbound news feed item for a participant"
  [from to occurred subject story]
  {:from from :to to :occurred occurred :subject subject :story story})
