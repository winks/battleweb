(ns battleweb.db
  (:use battleweb.settings))

(cond
  (= "h2" bw-db-engine)
    (let [db-protocol "file"
          db-host bw-db-host
          db-name bw-db-name]
      (def bw-db {:classname "org.h2.Driver",
                  :subprotocol "h2"
                  :subname (str db-protocol "://" db-host "/" db-name),
                  :user bw-db-user,
                  :password bw-db-password}))
  (= "mysql" bw-db-engine)
    (let [db-host bw-db-host
          db-port bw-db-port
          db-name bw-db-name]
      (def bw-db {:classname "com.mysql.jdbc.Driver"
                  :subprotocol "mysql"
                  :subname (str "//" db-host ":" db-port "/" db-name)
                  :user bw-db-user
                  :password bw-db-password}))
  :else
    (let [db-host bw-db-host
          db-port bw-db-port
          db-name bw-db-name]
      (def bw-db {:classname "org.postgresql.Driver"
                  :subprotocol "postgresql"
                  :subname (str "//" db-host ":" db-port "/" db-name)
                  :user bw-db-user
                  :password bw-db-password})))


(cond
  (= "h2" bw-db-engine)
    (def bw-db-clob true)
  :else
    (def bw-db-clob false))
