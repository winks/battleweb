(ns battleweb.views.main
  (:require [battleweb.views.common :as common]
            [battleweb.models.character :as character]
            [battleweb.models.guild :as guild]
            [battleweb.models.item :as item]
            [battleweb.models.realm :as realm]
            [battleweb.models.storage :as storage])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
        battleweb.helper))

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
       " - Item info for 'Wrap of the Great Turtle'"]
      [:li
       (link-to "/character/us/ysondre/kripparrian" "/character/us/ysondre/kripparrian")
       " - Character info for Kripparrian on US-Ysondre"]
      ]]))

(defpage "/realm/:region/:name" {:keys [region name]}
  (bw-page "Realms!" (common/realms-list-full region (realm/get-realm region name))))

(defpage "/realm/:region" {:keys [region]}
  (bw-page "Realms!" (common/realms-list-full region (realm/get-realm region))))

(defpage "/guild/:region/:realm/:name" {:keys [region realm name]}
  (let [input (guild/get-guild region realm name)]
    (bw-page
      "Guilds!"
      (valid-char? input region common/guild-info common/has-error))))

(defpage "/item/:id" {:keys [id]}
  (bw-page "Items!" (common/item-info "eu" (item/get-item  "eu" id))))

(defpage "/character/:region/:realm/:name" {:keys [region realm name]}
  (bw-page
    "Characters!"
    (common/character-detail
      region
      (character/get-character-db region (slugify-realm realm) name))))

(defpage "/list/:listname" {:keys [listname]}
  (bw-page "Lists!" (common/char-table (character/get-character-list listname))))

(defpage "/lists/:listname" {:keys [listname]}
  (bw-page "Lists!" (common/char-table (character/get-character-list-db listname))))

(defpage "/load/:region/:realm/:name" {:keys [region realm name]}
  (let [input (storage/chars-table-select region realm name)]
    (bw-page
      "Load"
      (valid-char? input region common/character-detail common/has-error)))) 

;(defpage "/sql/create/listsdb" {}
;  (storage/lists-table-create))