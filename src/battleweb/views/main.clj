(ns battleweb.views.main
  (:require [battleweb.views.common :as common]
            [battlenet.core :as bnc]
            [battlenet.network :as bnn]
            [battleweb.models.item :as item])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpartial bw-page
  [headline content]
  (common/layout
    [:h1 headline]
    content))

(defpage "/" []
         (common/layout
           [:h1 "Welcome to " (link-to "https://github.com/winks/battleweb" "battleweb")]
           [:p "Some example pages, feel free to play around with urls:"]
           [:ul
            [:li (link-to "/realm/eu/malygos" "/realm/eu/malygos") " - Realm info for EU-Malygos"]
            [:li (link-to "/realm/eu" "/realm/eu") " - All EU realms"]
            [:li (link-to "/realm/us" "/realm/us") " - All US realms"]
            [:li
             (link-to "/guild/us/ysondre/exodus" "/guild/us/ysondre/exodus") 
             " - Guild info for Exodus on US-Ysondre"]
            [:li
             (link-to "/item/62383" "/item/62383")
             " - Item info for 'Wrap of the Great Turtle'"]]))

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
  (bw-page "Items!" (common/item-info "eu" (item/get-item  "eu" id))))