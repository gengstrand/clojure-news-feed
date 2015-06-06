(ns feed.settings)

(def config-file (System/getenv "APP_CONFIG"))
(def service-config (if (nil? config-file) nil (load-file config-file)))

