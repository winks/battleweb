(ns battleweb.models.character
  (:require [clojure.java.jdbc :as sql]
            [battlenet.core :as bnc]
            [battlenet.network :as bnn]
            [battlenet.tools :as bnt]
            [battleweb.models.storage :as storage]))

(defn get-character
  [region realm name]
  (bnn/read-remote-character
    region
    realm
    name
    (bnt/join-params ["fields" "guild" "titles" "professions"])))

(defn get-character-db
  [region realm name]
  (do
    (let [chr (get-character region realm name)]
      (storage/chars-table-update region realm name chr)
      (identity chr))))
    

(defn get-character-list
  [name]
  (let [clist [["us" "ysondre" "kripparrian"]
;               ["us" "ysondre" "kripp"]
;               ["us" "ysondre" "kripparron"]
               ]]
    (for [citem clist]
      [(nth citem 0) (get-character (nth citem 0) (nth citem 1) (nth citem 2))])))

