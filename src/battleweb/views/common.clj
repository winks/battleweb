(ns battleweb.views.common
  (:require [clojure.string :as string]
            [battlenet.tools :as bnt])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers
        battleweb.helper))

(defpartial bw-footer [& content]
  [:hr]
  (link-to "/" "Home"))

(defpartial layout [& content]
            (html5
              [:head
               [:title "battleweb"]
               (include-css "/css/reset.css")
               (include-css "/css/battleweb.css")]
              [:body
               [:div#wrapper
                content]
               [:div#footer
                (bw-footer)]]))


(defpartial has-error
  [_ & x]
  [:div#error "Error"])

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
    [:h3 {:class (.toLowerCase (bnt/lookup-faction side))}
     name " (" (.toUpperCase region) "-" realm ")"]
    [:span.points achievementPoints " Achievement Points"]
    [:div.item-footer
     (link-guild-a region realm name "[A]")]]])

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
       (bnt/lookup-stat (:stat statMap))])))

(defpartial item-stats-extended
;  "Displays stats: HIT/CRI/MST/EXP/etc"
  [stats]
  (for [statMap stats]
    (if (> (:stat statMap) 7)
      [:li.item-bonus
       (str
         "Equip: Increases your "
         (-> (string/lower-case (bnt/lookup-stat (:stat statMap))))
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
    [:li {:class (str "item-name " (.toLowerCase (bnt/lookup-quality quality)))} name]
    [:li (if (= 1 itemBind) "Binds when picked up" "Binds when equipped")]
    [:li (if (integer? inventoryType) (bnt/lookup-inventory (+ -1 inventoryType)) "")]
    [:li (if (integer? baseArmor) (str baseArmor " Armor"))]
    (item-stats-base bonusStats)
    [:li (str "Requires Level " requiredLevel)]
    [:li "Item Level " itemLevel]
    [:li.item-description description]
    (item-stats-extended bonusStats)
    [:li "Sell Price: " (bnt/copper-to-gold sellPrice)]
    [:li.item-footer
     (link-item-a region id "Armory") " "
     (link-item-wh id "Wowhead")]]])

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
        primary (bnt/get-primary-professions professions)
        primary-a (first primary)
        primary-b (nth primary 1)]
    [:div#character
     [:div.icon (image (bnt/media-url-avatar region thumbnail))]
     [:ul.inner
      [:li {:class (str "class-" (string/lower-case (bnt/lookup-class class)))}
       (->
         (bnt/get-title character)
         (string/replace "%s" name))
       " "
       "&lt;"
       (link-guild region realm (:name guild) (:name guild))
       "&gt;"]
      [:li level " " (bnt/lookup-race race) " " (bnt/lookup-class class)]
      [:li (-> (string/upper-case region)) "-" realm ", " achievementPoints " Points"]
      (if (not (string/blank? (first primary-a)))
        [:li (first primary-a) " " (nth primary-a 1)])
      (if (not (string/blank? (first primary-b)))
        [:li (first primary-b) " " (nth primary-b 1)])
      ]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defpartial char-td
  [char-class char-content]
  [:td
   {:class (str "cls-3d-" (string/lower-case (slugify-icon (bnt/lookup-class char-class))))}
   char-content])

(defpartial char-tr
  [input]
  (let [region (first input)
        character (nth input 1)
        {:keys [name
                realm
                level
                gender
                race
                class
                achievementPoints
                thumbnail
                lastModified
                guild
                professions]} character
        primary (bnt/get-primary-professions professions)
        primary-a (first primary)
        primary-b (nth primary 1)
        title (bnt/get-title character)
        talents (:talents character)]
    (if (string/blank? name)
      (identity "")
     [:tr
     (char-td class (str
                      (iconify-race (bnt/lookup-race race) (bnt/lookup-gender gender))
                      ""
                      (iconify-class (bnt/lookup-class class))))
     (char-td class (string/replace title "%s" (link-char region realm name name)))
     (char-td class level)
     (char-td class (str
                      (iconify-spell (nth (first (bnt/get-talent-spec talents)) 2))
                      " "
                      (iconify-spell (nth (nth (bnt/get-talent-spec talents) 1) 2))))
     (char-td class (str
                      (nth (first (bnt/get-talent-spec talents)) 1)
                      " "
                      (nth (nth (bnt/get-talent-spec talents) 1) 1)))
     (char-td class
              (let [guild-link (link-guild region (slugify-realm realm) (:name guild) (:name guild))]
                (if (string/blank? guild-link)
                  ""
                  (str "&lt;" guild-link "&gt;"))))
     (char-td class (if (string/blank? (first primary-a))
                      ""
                      (str (iconify-prof (first primary-a)) " " (nth primary-a 1))))
     (char-td class (if (string/blank? (first primary-b))
                      ""
                      (str (iconify-prof (first primary-b)) " " (nth primary-b 1))))
     (char-td class (bnt/get-secondary-profession professions "Cooking"))
     (char-td class (bnt/get-secondary-profession professions "First Aid"))
     (char-td class (bnt/get-secondary-profession professions "Fishing"))
     (char-td class achievementPoints)
     (char-td class (link-char-a region realm name "[A]"))])))

(defpartial char-table
  [characters]
  [:div {:align "center"}
  [:table#char-table
   [:thead
    [:th.cls-3d {:colspan 3} "Character"]
    [:th.cls-3d {:colspan 2} "Spec"]
    [:th.cls-3d {:colspan 1} "Guild"]
    [:th.cls-3d {:colspan 2} "Professions"]
    [:th.cls-3d {:colspan 1} (iconify-prof "Cooking")]
    [:th.cls-3d {:colspan 1} (iconify-prof "First Aid")]
    [:th.cls-3d {:colspan 1} (iconify-prof "Fishing")]
    [:th.cls-3d {:colspan 1} "AchP"]
    [:th.cls-3d {:colspan 1} "Links"]]
   (map char-tr characters)]])
