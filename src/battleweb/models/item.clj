(ns battleweb.models.item
  (:require [battlenet.network :as bnn]))

(defn get-item
  [region id]
  (bnn/read-remote-item region id))