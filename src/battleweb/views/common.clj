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

(defpartial slugify-icon
  [name]
  (string/capitalize (string/lower-case (string/replace name " " ""))))

(defpartial iconize-prof
  [prof]
  [:span
  [:img
   {:src
    (->
      "/img/ico/profession/{prof}.png"
      (string/replace "{prof}" (slugify-icon prof))),
    :width 20,
    :height 20,
    :alt prof,
    :title prof}]])

(defpartial iconize-class
  [class]
  [:img
   {:src
    (->
      "/img/ico/class/{class}.jpg"
      (string/replace "{class}" (slugify-icon class))),
    :width 20,
    :height 20,
    :alt class,
    :title class}])

(defpartial iconize-race
  [race gender]
  [:img
   {:src
    (->
      "/img/ico/race/IconLarge_{race}_{gender}.png"
      (string/replace "{race}" (slugify-icon race))
      (string/replace "{gender}" "Male"))
    :width 20,
    :height 20,
    :alt (str race " " gender),
    :title (str race " " gender)}])

(defpartial link-guild
  [region realm name text]
  (link-to (str "/guild/" region "/" realm "/" name) text))

(defpartial link-char
  [region realm name text]
  (link-to (str "/character/" region "/" realm "/" name) text))

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

(defpartial character-detail
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defpartial char-td
  [char-class char-content]
  [:td {:class (str "cls-3d-" (string/lower-case (slugify-icon (get bnd/bn-classes char-class))))}
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
        primary (bnt/get-primary-professions professions)
        primary-a (first primary)
        primary-b (nth primary 1)
        title (bnt/get-title character)
        linked-name (link-char region realm name name)]
    [:tr
     (char-td class (str
                      (iconize-race (get bnd/bn-races race) gender)
                      ""
                      (iconize-class (get bnd/bn-classes class))))
     (char-td class (string/replace title "%s" (link-char region realm name name)))
     (char-td class level)
     (char-td class (str "&lt;" (link-guild region realm (:name guild) (:name guild)) "&gt;"))
     (char-td class (if (string/blank? (first primary-a))
                      ""
                      (str (iconize-prof (first primary-a)) " " (nth primary-a 1))))
     (char-td class (if (string/blank? (first primary-b))
                      ""
                      (str (iconize-prof (first primary-b)) " " (nth primary-b 1))))
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
    [:th.cls-3d {:colspan 1} (iconize-prof "Cooking")]
    [:th.cls-3d {:colspan 1} (iconize-prof "First Aid")]
    [:th.cls-3d {:colspan 1} (iconize-prof "Fishing")]
    [:th.cls-3d {:colspan 1} "AchP"]]
   (let [num (count characters)
         regions (take num (cycle [region]))]
     (map char-tr regions characters))])