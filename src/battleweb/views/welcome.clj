(ns battleweb.views.welcome
  (:require [battleweb.views.common :as common]
            [noir.content.pages :as pages]
            [battlenet.core :as bnc])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpage "/welcome" []
         (common/layout
           [:h1 "Welcome"]
           [:p "Welcome to battleweb"]))

(defpage "/todos" {}
  (let [items common/all-todos]
    (common/layout
      [:h1 "Todo list!"]
      (common/todos-list items))))

(defpage "/realm/:region/:name" {:keys [region name]}
  (let [realms (bnc/realm-get-info region name)]
    (common/layout
      [:h1 "Realms!"]
      (common/realms-list realms))))