(ns battleweb.views.common
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpartial layout [& content]
            (html5
              [:head
               [:title "battleweb"]
               (include-css "/css/reset.css")]
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
  [{:keys [name status slug]}]
  [:li {:id slug}
   [:h3 name]
   [:span.status status]])

(defpartial realms-list
  [realms]
  [:ul#realmItems
   (map realm-item realms)])