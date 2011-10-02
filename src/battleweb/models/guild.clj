(ns battleweb.models.guild
  (:require [battlenet.core :as bnc]
            [battlenet.network :as bnn]))

(defn get-guild
  [region realm name]
  (bnn/read-remote-guild region realm name))