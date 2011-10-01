(ns battleweb.views.welcome
  (:require [battleweb.views.common :as common]
            [battlenet.core :as bnc]
            [battlenet.network :as bnn])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpage "/" []
         (common/layout
           [:h1 (str "Welcome to ") [:a {:href "https://github.com/winks/battleweb"} "battleweb"]]
           [:p "Some example pages:"]
           [:ul
            [:li [:a {:href "/realm/eu/malygos"}
                  "/realm/eu/malygos"] " - Realm info for EU-Malygos"]
            [:li [:a {:href "/realm/eu"}
                  "/realm/eu"] " - All EU realms"]
            [:li [:a {:href "/realm/us"}
                  "/realm/us"] " - All US realms"]
            [:li [:a {:href "/guild/us/ysondre/exodus"}
                  "/guild/us/ysondre/exodus"] " - Guild info for Exodus on US-Ysondre"]
            [:li [:a {:href "/item/62383"}
                  "/item/62383"] " - Item info for 'Wrap of the Great Turtle'"]]))

(defpage "/realm/:region/:name" {:keys [region name]}
  (let [realms (bnc/realm-get-info region name)]
    (common/layout
      [:h1 "Realms!"]
      (common/realms-list-full realms))))

(defpage "/realm/:region" {:keys [region]}
  (let [realms (bnc/get-realm-names region)]
    (common/layout
       [:h1 "Realms!"]
       (common/realms-list-basic region realms))))

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