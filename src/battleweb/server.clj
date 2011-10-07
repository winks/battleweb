(ns battleweb.server
  (:require [noir.server :as server])
  (:use battleweb.settings))

(server/load-views "src/battleweb/views/")

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" bw-port))]
    (server/start port {:mode mode
                        :ns 'battleweb})))

