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

(defpartial iconize-prof
  [prof]
  (let [parts (string/split prof #" ")]
    [:span
    [:img
     {:src
      (->
        "/img/ico/profession/%s.png"
        (string/replace "%s" (first parts))),
      :width 20,
      :height 20}] " " (nth parts 1)]))

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
;  "Displays guild info"
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

(defpartial item-stats-base
;  "Displays base stats: AGI/STR/INT/SPI/STA"
  [stats]
  (for [statMap stats]
    (if (<= (:stat statMap) 7)
      [:li
       (if (< (:amount statMap) 0) "-" "+")
       (:amount statMap)
       " "
       (get bnd/bn-stats (:stat statMap))])))

(defpartial item-stats-extended
;  "Displays stats: HIT/CRI/MST/EXP/etc"
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

(defpartial item-info
;  "Displays tooltip-like item information"
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defpartial character-info
  [region character]
  (let [{:keys [name
                realm
                level
                gender
                race
                class
                achievementPoints
                thumbnail
                lastModified
                guild
                titles
                professions]} character
        primary (bnt/get-primary-professions professions)]
    [:div#character
     [:div.icon (image (bnt/media-url-avatar region thumbnail))]
     [:ul.inner
      [:li {:class (str "class-" (-> (string/lower-case (get bnd/bn-classes class))))}
       (->
         (bnt/get-title character)
         (string/replace "%s" name))
       " "
       "&lt;"
       (link-to (str "/guild/" region "/" realm "/" (:name guild)) (:name guild))
       "&gt;"]
      [:li level " " (get bnd/bn-races race) " " (get bnd/bn-classes class)]
      [:li (-> (string/upper-case region)) "-" realm ", " achievementPoints " Points"]
      (if (not (string/blank? (first primary))) [:li (first primary)])
      (if (not (string/blank? (nth primary 1))) [:li (nth primary 1)])
      ]]))

(defpartial char-td
  [char-class char-content]
  [:td {:class (str "cls-3d-" (string/lower-case (get bnd/bn-classes char-class)))}
   char-content])

(defpartial char-tr
  [region character]
  (let [{:keys [name
                realm
                level
                gender
                race
                class
                achievementPoints
                thumbnail
                lastModified
                guild
                titles
                professions]} character
        primary (bnt/get-primary-professions professions)]
    [:tr
     (char-td class (str (get bnd/bn-races race) " " (get bnd/bn-classes class)))
     (char-td class name)
     (char-td class level)
     (char-td class (link-to (str "/guild/" region "/" realm "/" (:name guild)) (:name guild)))
     (char-td class (if (not (string/blank? (first primary))) (iconize-prof (first primary)) ""))
     (char-td class (if (not (string/blank? (nth primary 1))) (iconize-prof (nth primary 1)) ""))
     (char-td class (bnt/get-secondary-profession professions "Cooking"))
     (char-td class (bnt/get-secondary-profession professions "First Aid"))
     (char-td class (bnt/get-secondary-profession professions "Fishing"))
     (char-td class achievementPoints)
     ]))

(defpartial char-table
  [region characters]
  [:table#char-table
   [:thead
    [:th.cls-3d {:colspan 3} "Character"]
    [:th.cls-3d {:colspan 1} "Guild"]
    [:th.cls-3d {:colspan 2} "Professions"]
    [:th.cls-3d {:colspan 1} "Coo"]
    [:th.cls-3d {:colspan 1} "Fir"]
    [:th.cls-3d {:colspan 1} "Fis"]
    [:th.cls-3d {:colspan 1} "AchP"]]
   (let [num (count characters)
         regions (take num (cycle [region]))]
     (map char-tr regions characters))])