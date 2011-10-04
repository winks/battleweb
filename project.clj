(defproject battleweb "0.1.0-SNAPSHOT"
            :description "A web frontend for battlenet."
            :dependencies [[org.clojure/clojure "1.2.1"]
                           [noir "1.1.0"]
                           [battlenet "0.0.6"]]
            :main battleweb.server)