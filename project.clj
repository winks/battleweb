(defproject battleweb "0.1.0-SNAPSHOT"
            :description "A web frontend for battlenet."
            :dependencies [[org.clojure/clojure "1.2.1"]
                           [org.clojure/java.jdbc "0.0.6"]
                           [noir "1.1.0"]
                           [battlenet "0.1.2"]
                           [com.h2database/h2 "1.3.160"]
                           [mysql/mysql-connector-java "5.1.18"]
                           [postgresql/postgresql "9.0-801.jdbc4"]]
            :dev-dependencies [[lein-ring "0.4.6"]]
            :ring {:handler battleweb.server/handler}
            :main battleweb.server)
