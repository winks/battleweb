(ns battleweb.settings)

(let [db-protocol "file"
        db-host "/d:/battleweb"
        db-name "battleweb"]
    (def bw-db {:classname "org.h2.Driver",
                :subprotocol "h2"
                :subname (str db-protocol "://" db-host "/" db-name),
                :user "sa",
                :password ""}))