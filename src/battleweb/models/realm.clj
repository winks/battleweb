(ns battleweb.models.realm
  (:require [battlenet.core :as bnc]
            [battlenet.network :as bnn])
  (:use battleweb.helper))

(defn get-realm
  ([region]
    (bnc/realm-get-info region ""))
  ([region name]
    (bnc/realm-get-info region name)))