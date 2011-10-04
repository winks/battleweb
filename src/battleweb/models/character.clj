(ns battleweb.models.character
  (:require [battlenet.core :as bnc]
            [battlenet.network :as bnn]
            [battlenet.tools :as bnt]))

(defn get-character
  [region realm name]
  (bnn/read-remote-character
    region
    realm
    name
    (bnt/join-params ["fields" "guild" "titles" "professions"])))

(defn get-character-list
  [name]
  (let [clist [["us" "ysondre" "kripparrian"]
;               ["us" "ysondre" "kripp"]
;               ["us" "ysondre" "kripparron"]
               ]]
    (for [citem clist]
      (get-character (nth citem 0) (nth citem 1) (nth citem 2)))))