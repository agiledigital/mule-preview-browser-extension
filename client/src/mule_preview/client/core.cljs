(ns mule-preview.client.core
  (:require
   [mule-preview.client.transformer :refer [transform-xml-to-components]]
   [reagent.core :as r]
   [tubax.core :refer [xml->clj]]
   [mule-preview.client.views.preview :refer [start-preview]]
   [mule-preview.client.views.diff :refer [start-diff]]))

(def root-component (r/atom [:div]))

;; -------------------------
;; Views

(defn preview-view []
  (start-preview "/example_xml/nice-example.xml" root-component)
  [:div {:class "root-component"} @root-component])

(defn diff-view []
  (start-diff "/example_xml/nice-example.xml" 
              "/example_xml/nice-example-diff.xml" 
              root-component)
  [:div {:class "root-component"} @root-component])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [preview-view] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
