(ns mule-preview.client.core
  (:require
   [reagent.core :as r]
   [tubax.core :refer [xml->clj]]
   [mule-preview.client.views.preview :refer [start-preview]]
   [mule-preview.client.views.diff :refer [start-diff]]))

(def root-component (r/atom [:div]))

;; -------------------------
;; Views

(defn view []
  [:div {:class "root-component"} @root-component])

(start-diff "/example_xml/nice-example.xml"
            "/example_xml/nice-example-diff.xml"
            root-component)
; (start-preview "/example_xml/nice-example.xml" root-component)


;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [view] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
