(ns battleweb.views.main
  (:require [battleweb.views.common :as common]
            [battleweb.models.guild :as guild]
            [battleweb.models.item :as item]
            [battleweb.models.realm :as realm])
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
    [:div "Some example pages, feel free to play around with urls:"
     [:ul
      [:li (link-to "/realm/eu/malygos" "/realm/eu/malygos") " - Realm info for EU-Malygos"]
      [:li (link-to "/realm/eu" "/realm/eu") " - All EU realms"]
      [:li (link-to "/realm/us" "/realm/us") " - All US realms"]
      [:li
       (link-to "/guild/us/ysondre/exodus" "/guild/us/ysondre/exodus") 
       " - Guild info for Exodus on US-Ysondre"]
      [:li
       (link-to "/item/62383" "/item/62383")
       " - Item info for 'Wrap of the Great Turtle'"]]]))

(defpage "/realm/:region/:name" {:keys [region name]}
  (bw-page "Realms!" (common/realms-list-full (realm/get-realm region name))))

(defpage "/realm/:region" {:keys [region]}
  (bw-page "Realms!" (common/realms-list-full (realm/get-realm region))))

(defpage "/guild/:region/:realm/:name" {:keys [region realm name]}
  (bw-page "Guilds!" (common/guild-info "eu" (guild/get-guild  region realm name))))

(defpage "/item/:id" {:keys [id]}
  (bw-page "Items!" (common/item-info "eu" (item/get-item  "eu" id))))