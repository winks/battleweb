(ns battleweb.models.guild
  (:require [battlenet.core :as bnc]
            [battlenet.network :as bnn])
  (:use battleweb.helper))

(defn get-guild
  [region realm name]
  (bnn/read-remote-guild region realm name))