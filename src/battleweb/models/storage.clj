(ns battleweb.models.storage
  (:require [clojure.string :as string]
            [clojure.java.jdbc :as sql])
  (:use [clojure.data.json :only (json-str write-json read-json)]
        battleweb.helper
        battleweb.settings
        battleweb.db))

(defn create-uuid
  "Create a unique key of <region>_<realm>_<name>"
  [region realm name]
  (str (string/lower-case region)
       "_"
       (slugify-realm realm)
       "_"
       (string/lower-case name)))

(defn declob
  "Decode weird character objects.
  @see http://en.wikibooks.org/wiki/Clojure_Programming/Examples/JDBC_Examples#H2Database
  "
  [clob]
  (with-open [rdr (java.io.BufferedReader. (.getCharacterStream clob))]
                 (apply str (line-seq rdr))))

;;;;;;;;;;;;;;;;;;
; characters - sql
;;;;;;;;;;;;;;;;;;
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
    ["eu_kazzak_znufflessd" "foo" "2011-10-04"]))

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
  (try
    (sql/with-query-results
      rs
      ["SELECT * FROM bwchars WHERE id = ?" id]
      (if (= true bw-db-clob)
        (declob (:content (first rs)))
        (:content (first rs))))
    (catch Exception e
      (do
        (println (.printStackTrace e))
        (identity nil)))))

;;;;;;;;;;;;;;;;;;
; characters - api
;;;;;;;;;;;;;;;;;;

(defn chars-table-create
  []
  (sql/with-connection
    bw-db
    (sql/transaction
      (sql-chars-create))))

(defn chars-table-insert
  []
  (sql/with-connection
    bw-db
    (sql/transaction
      (sql-chars-insert))))

(defn chars-table-update
  [region realm name text]
  (sql/with-connection
    bw-db
    (sql/transaction
      (sql-chars-update
        (create-uuid region realm name)
        (json-str text)))))

(defn chars-table-select
  [region realm name]
  (if-let [result (sql/with-connection
                    bw-db
                    (sql/transaction
                      (sql-chars-select (create-uuid region realm name))))]
    (do
      (read-json result))
    (do
      (bwlog "warn" "chars-table-select failed")
      (identity nil))))

;;;;;;;;;;;;;
; lists - sql
;;;;;;;;;;;;;
(defn- sql-lists-create
  []
  (sql/create-table
      :bwlists
      [:id "varchar(255)" "PRIMARY KEY"]
      [:content :text]
      [:updated :text]))

(defn- sql-lists-select
  [listid]
  (try
    (sql/with-query-results
      rs
      ["SELECT * FROM bwlists WHERE id = ?" listid]
      (if (= true bw-db-clob)
        (declob (:content (first rs)))
        (:content (first rs))))
    (catch Exception e
      (do
        (bwlog "debug" (.printStackTrace e))
        (identity nil)))))

;;;;;;;;;;;;;
; lists - api
;;;;;;;;;;;;;
(defn lists-table-create
  []
  (sql/with-connection
    bw-db
    (sql/transaction
      (sql-lists-create))))

(defn lists-table-select
  [listid]
  (if-let [result (sql/with-connection
                    bw-db
                      (sql/transaction
                        (sql-lists-select listid)))]
    (do
      (read-json result))
    (do
      (bwlog "warn" "lists-table-select failed")
      (identity nil))))
