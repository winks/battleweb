(ns battleweb.models.guild
  (:require [battlenet.core :as bnc]
            [battlenet.network :as bnn])
  (:use battleweb.helper))

(defn get-guild
  [region realm name]
  (let [sname (slugify-name name)]
    (do
      (bwlog "debug" sname)
    (bnn/read-remote-guild region realm sname))))