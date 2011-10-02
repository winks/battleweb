(ns battleweb.models.realm
  (:require [battlenet.core :as bnc]
            [battlenet.network :as bnn]))

(defn get-realm
  ([region]
    (bnc/realm-get-info region ""))
  ([region name]
    (bnc/realm-get-info region name)))