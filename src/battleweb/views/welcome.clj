(ns battleweb.views.welcome
  (:require [battleweb.views.common :as common]
            [noir.content.pages :as pages]
            [battlenet.core :as bnc]
            [battlenet.network :as bnn])
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

(defpage "/guild/:region/:realm/:name" {:keys [region realm name]}
  (let [guild (bnn/read-remote-guild region realm name)]
    (common/layout
      [:h1 "Guilds!"]
      (common/guild-info region guild))))

(defpage "/item/:id" {:keys [id]}
  (let [item (bnn/read-remote-item "eu" id)]
    (common/layout
      [:h1 "Items!"]
      (common/item-info "eu" item))))