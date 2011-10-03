(ns battleweb.views.common
  (:require [clojure.string :as string]
            [battlenet.defs :as bnd]
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
    [:li {:class (str "item-name " (.toLowerCase (get bnd/bn-quality quality)))} name]
    [:li (if (= 1 itemBind) "Binds when picked up" "Binds when equipped")]
    [:li (if (integer? inventoryType) (get bnd/bn-inventory (+ -1 inventoryType)) "")]
    [:li (if (integer? baseArmor) (str baseArmor " Armor"))]
    (item-stats-base bonusStats)
    [:li (str "Requires Level " requiredLevel)]
    [:li "Item Level " itemLevel]
    [:li.item-description description]
    (item-stats-extended bonusStats)
    [:li "Sell Price: " (bnt/copper-to-gold sellPrice)]
    [:li.item-footer
     (link-to (str "http://" region ".battle.net/wow/en/item/" id) "Armory") " "
     (link-to (str "http://www.wowhead.com/item=" id) "Wowhead")]]])

(defpartial item-stats-base
  [stats]
  (for [statMap stats]
    (if (<= (:stat statMap) 7)
      [:li
       (if (< (:amount statMap) 0) "-" "+")
       (:amount statMap)
       " "
       (get bnd/bn-stats (:stat statMap))])))

(defpartial item-stats-extended
  [stats]
  (for [statMap stats]
    (if (> (:stat statMap) 7)
      [:li.item-bonus
       (str
         "Equip: Increases your "
         (-> (string/lower-case (get bnd/bn-stats (:stat statMap))))
         " rating by "
         (:amount statMap)
         ".")])))