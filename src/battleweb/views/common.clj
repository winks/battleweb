(ns battleweb.views.common
  (:require [battlenet.defs :as bnd]
            [battlenet.tools :as bnt])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpartial layout [& content]
            (html5
              [:head
               [:title "battleweb"]
               (include-css "/css/reset.css")
               (include-css "/css/battleweb.css")]
              [:body
               [:div#wrapper
                content]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defpartial realm-item-full
  [{:keys [name status slug type queue population battlegroup]}]
  [:li {:id slug, :class (if (= true status) "online" "offline")}
   [:h3 name " (" battlegroup ") [" type "]"]
   [:span.pop "Pop: " population " (" (if (= false queue) "No " "Has ") "Queue)"]])

(defpartial realms-list-full
  [realms]
  [:ul#realmItems
   (map realm-item-full realms)])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defpartial guild-info
  [region {:keys [name realm level side achievementPoints emblem lastModified]}]
  [:div#guild
   [:div.inner
    [:div.level level]
    [:h3 {:class (.toLowerCase (get bnd/bn-factions side))}
     name " (" (.toUpperCase region) "-" realm ")"]
    [:span.points achievementPoints " Achievement Points"]
    [:div.item-footer
     (link-to
       (str "http://" region ".battle.net/wow/guild/" realm "/" name "/")
       "Armory")]]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defpartial item-info
  [region {:keys [name id description requiredLevel quality
                  itemBind itemClass baseArmor itemLevel inventoryType
                  sellPrice bonusStats icon]}]
  [:div#item
   [:div {:class "icon"}
    [:img {:src (bnt/media-url-icon region "wow" "large" icon),
           :alt "Shiny!",
           :title "Shiny!"}]]
   [:div.inner
    [:h3 {:class (.toLowerCase (get bnd/bn-quality quality))} name]
    [:span (if (= 1 itemBind) "Binds when picked up" "Binds when equipped")] [:br]
    [:span (if (integer? inventoryType) (get bnd/bn-inventory (+ -1 inventoryType)) "")] [:br]
    [:span (if (integer? baseArmor) (str baseArmor " Armor"))] [:br]
    [:span (str "Requires Level " requiredLevel)] [:br]
    [:span "Item Level " itemLevel] [:br]
    [:span.item-description description]
    [:span "Sell Price: " (bnt/copper-to-gold sellPrice)] [:br]
    [:div.item-footer
     (link-to (str "http://" region ".battle.net/wow/en/item/" id) "Armory") " "
     (link-to (str "http://www.wowhead.com/item=" id) "Wowhead")]]])