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

(defpartial todo-item
  [{:keys [id title due]}]
  [:li {:id id}
   [:h3 title]
   [:span.due due]])

(defpartial todos-list
  [items]
  [:ul#todoItems
   (map todo-item items)])
  
(def all-todos
  [{:id "todo1"
    :title "Get Milk"
    :due "today"}])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defpartial realm-item
  [{:keys [name status slug type queue population battlegroup]}]
  [:li {:id slug, :class (if (= true status) "online" "offline")}
   [:h3 (str name " (" battlegroup ") [" type "]")]
   [:span.pop (str "Pop: " population " (" (if (= false queue) "No ") "Queue)")]])


(defpartial realms-list
  [realms]
  [:ul#realmItems
   (map realm-item realms)])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defpartial guild-info
  [region {:keys [name realm level side achievementPoints emblem lastModified]}]
  [:div#guild
   [:div.inner
    [:div.level level]
    [:h3 {:class (.toLowerCase (nth bnd/bn-factions side))}
     (str name " (" (.toUpperCase region) "-" realm ")")]
    [:span.points (str achievementPoints " Achievement Points")]]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defpartial item-info
  [region {:keys [name id description requiredLevel quality
                  itemBind itemClass baseArmor itemLevel inventoryType
                  sellPrice bonusStats icon]}]
  [:div#item
   [:div {:class "icon"} [:img {:src (bnt/media-url-icon region "wow" "large" icon)}]]
   [:div.inner
    [:h3 {:class (.toLowerCase (nth bnd/bn-quality quality))}
     (str name)]
    [:span (if (= 1 itemBind) "Binds when picked up" "Binds when equipped")] [:br]
    [:span (if (integer? inventoryType) (nth bnd/bn-inventory (+ -1 inventoryType)) "")] [:br]
    [:span (if (integer? baseArmor) (str baseArmor " Armor"))] [:br]
    [:span (str "Requires Level " requiredLevel)] [:br]
    [:span (str "Item Level " itemLevel)] [:br]
    [:span.item-description description]
    [:span (str "Sell Price: "
                (quot sellPrice 10000) " G " 
                (str (quot (- sellPrice (* 10000 (quot sellPrice 10000))) 100) " S ")
                (mod sellPrice 1000) " C")] [:br]
    [:div.item-footer
     [:a {:href (str "http://" region ".battle.net/wow/en/item/" id)} "Armory"] " "
    [:a {:href (str "http://www.wowhead.com/item=" id)} "Wowhead"]]]])