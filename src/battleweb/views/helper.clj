(ns battleweb.views.helper
  (:require [clojure.string :as string])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defn bwlog
  [level & msgparts]
   (println (str "bw: [" level "]") (apply str msgparts)))

(defn bwinfo
  [msg & parts]
  (bwlog "info" msg (apply str parts)))

(defn slugify-icon
  [name]
  (string/capitalize (string/lower-case (string/replace name " " ""))))

(defn slugify-realm
  [name]
  (->
    name
    (string/replace "'" "")
    (string/replace " " "-")
    (string/lower-case)))

(defpartial iconify-prof
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

(defpartial iconify-class
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

(defpartial iconify-race
  [race gender]
  [:img
   {:src
    (->
      "/img/ico/race/IconLarge_{race}_{gender}.png"
      (string/replace "{race}" (slugify-icon race))
      (string/replace "{gender}" gender))
    :width 20,
    :height 20,
    :alt (str race " " gender),
    :title (str race " " gender)}])

(defpartial link-guild
  [region realm name text]
  (link-to (str "/guild/" region "/" (slugify-realm realm) "/" (string/replace name " " "%20")) text))

(defpartial link-char
  [region realm name text]
  (link-to (str "/character/" region "/" (slugify-realm realm) "/" name) text))

(defpartial link-realm
  [region name]
  (link-to (str "/realm/" region "/" (slugify-realm name))))

(defpartial link-guild-a
  [region realm name text]
  (link-to
       (str
         "http://"
         region
         ".battle.net/wow/guild/"
         (slugify-realm realm)
         "/"
         (string/replace name " " "%20")
         "/")
       text))

(defpartial link-char-a
  [region realm name text]
  (link-to
       (str "http://" region ".battle.net/wow/character/" (slugify-realm realm) "/" name "/")
       text))

(defpartial link-item-a
  [region id text]
  (link-to
       (str "http://" region ".battle.net/wow/item/" id)
       text))

(defpartial link-item-wh
  [id text]
  (link-to
       (str "http://www.wowhead.com/item=" id)
       text))

(defpartial valid-char?
  [input region fnyes fnno]
  (if-let [name (:name input)]
    (do
;      (bwlog "debug" "valid:" name)
      (fnyes region input))
    (do
      (bwlog "debug" "invalid:" input)
      (fnno region input))))