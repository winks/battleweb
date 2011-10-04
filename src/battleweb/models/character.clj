(ns battleweb.models.character
  (:require [clojure.java.jdbc :as sql]
            [battlenet.core :as bnc]
            [battlenet.network :as bnn]
            [battlenet.tools :as bnt]
            [battleweb.models.storage :as storage])
  (:use battleweb.helper))

(defn get-character
  "Grab a character."
  [region realm name]
  (bnn/read-remote-character
    region
    realm
    name
    (bnt/join-params ["fields" "guild" "titles" "professions"])))

(defn get-character-db
  "Grab a character - DB version."
  [region realm name]
  (if-let [chr (storage/chars-table-select region realm name)]
    (do
;      (bwlog "debug" "char from db  (" region "/" realm "/" name ")")
      (identity chr))
    (do
      (bwlog "debug" "char from net (" region "/" realm "/" name ")")
      (let [chr (get-character region realm name)]
        (storage/chars-table-update region realm name chr)
        (identity chr)))))

(defn get-character-list
  "Grab a list."
  ([name]
    (let [clist [["us" "ysondre" "kripparrian"]]]
      (for [citem clist]
        [(nth citem 0) (get-character (nth citem 0) (nth citem 1) (nth citem 2))])))
  ([name clist]
    (for [citem clist]
      [(nth citem 0) (get-character-db (nth citem 0) (nth citem 1) (nth citem 2))])))

(defn get-character-list-db
  "Grab a list - DB version."
  [listname]
  (if-let [clist (storage/lists-table-select listname)]
    (do
      (get-character-list listname clist))
    (do
      (bwlog "debug" "Fake list")
      (let [clist (get-character-list listname)]
        (identity clist)))))