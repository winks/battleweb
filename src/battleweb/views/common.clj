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
  [region {:keys [name status slug type queue population battlegroup]}]
  [:li {:id (str region "-" slug), :class (if (= true status) "online" "offline")}
   [:span.realmtype  "[" type "] "]
   [:span (link-to (str "/realm/" region "/" slug) name) " (" battlegroup ")"]
   [:span.pop population " pop (" (if (= false queue) "No " "Has ") "Queue)"]])

(defpartial realms-list-full
  [region realms]
  [:ul#realmItems
   (let [num (count realms)
         regions (take num (cycle [region]))]
     (map realm-item-full regions realms))])

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
   [:ul.inner
    [:li {:class (.toLowerCase (get bnd/bn-quality quality))} name]
    [:li (if (= 1 itemBind) "Binds when picked up" "Binds when equipped")]
    [:li (if (integer? inventoryType) (get bnd/bn-inventory (+ -1 inventoryType)) "")]
    [:li (if (integer? baseArmor) (str baseArmor " Armor"))]
    [:li (str "Requires Level " requiredLevel)]
    [:li "Item Level " itemLevel]
    [:li.item-description description]
    [:li "Sell Price: " (bnt/copper-to-gold sellPrice)]
    [:li.item-footer
     (link-to (str "http://" region ".battle.net/wow/en/item/" id) "Armory") " "
     (link-to (str "http://www.wowhead.com/item=" id) "Wowhead")]]])