(ns battleweb.models.storage
  (:require [clojure.string :as string]
            [clojure.java.jdbc :as sql])
  (:use [clojure.data.json :only (json-str write-json read-json)])
  (:use battleweb.views.helper))

(let [db-protocol "file"
        db-host "/d:/battleweb"
        db-name "battleweb"]
    (def db {:classname "org.h2.Driver",
             :subprotocol "h2"
             :subname (str db-protocol "://" db-host "/" db-name),
             :user "sa",
             :password ""}))

(defn create-uuid
  [region realm name]
  (str (string/lower-case region)
       "_"
       (slugify-realm realm)
       "_"
       (string/lower-case name)))

(defn declob
  [clob]
  (with-open [rdr (java.io.BufferedReader. (.getCharacterStream clob))]
                 (apply str (line-seq rdr))))

(defn- sql-chars-create
  []
  (sql/create-table
      :bwchars
      [:id "varchar(255)" "PRIMARY KEY"]
      [:content :text]
      [:updated :text]))

(defn- sql-chars-insert
  []
  (sql/insert-values
    :bwchars
    [:id :content :updated]
    ["us_ysondre_kripparrian" "foo" "2011-10-04"]))

(defn- sql-chars-update
  [id text]
  (sql/update-or-insert-values
    :bwchars
    ["id=?" id]
    {:id id,
     :content text,
     :updated (. (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm:ss") format (java.util.Date.))}))

(defn- sql-chars-select
  [id]
  (sql/with-query-results rs ["SELECT * FROM bwchars WHERE id = ?" id]
                          (read-json (declob (:content (first rs))))))

(defn chars-table-create
  []
  (sql/with-connection
    db
    (sql/transaction
      (sql-chars-create))))

(defn chars-table-insert
  []
  (sql/with-connection
    db
    (sql/transaction
      (sql-chars-insert))))

(defn chars-table-update
  [region realm name text]
  (sql/with-connection
    db
    (sql/transaction
      (sql-chars-update
        (create-uuid region realm name)
        (json-str text)))))

(defn chars-table-select
  [region realm name]
  (let [id (create-uuid region realm name)]
    (sql/with-connection
      db
      (sql/transaction
        (sql-chars-select id)))))