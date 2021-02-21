(ns feed.daos.outbound)

(defn fetch
  "fetch the outbound news feed items for a participant"
  [id]
  [{:from 1 :occurred "2021-02-18" :subject "test" :story "test story"}])

(defn create
  "create an outbound news feed item for a participant"
  [from occurred subject story]
  {:from from :occurred occurred :subject subject :story story})

(defn search
  "search participants who posted this content"
  [keywords]
  [1])

(defn index
  "index participant story as searchable"
  [from story])