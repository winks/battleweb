(ns battleweb.models.item
  (:require [battlenet.core :as bnc]
            [battlenet.network :as bnn]))

(defn get-item
  [region id]
  (bnn/read-remote-item region id))