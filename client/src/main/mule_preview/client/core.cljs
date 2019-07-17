(ns mule-preview.client.core
  "The entry point to the web application"
  (:require
   [reagent.core :as r]
   [tubax.core :refer [xml->clj]]
   [mule-preview.client.views.preview :refer [start-preview]]
   [mule-preview.client.views.diff :refer [start-diff]]))

(def root-component (r/atom [:div]))

(defn view [root-component]
  [:div {:class "root-component"} @root-component])

(defn mount-root [element root-component]
  (r/render [(partial view root-component)] element))

(defn ^:export mount-diff-on-element [element file-a file-b]
  (let [root-component (r/atom [:div])]
    (start-diff file-a
                file-b
                root-component)
    (mount-root element root-component)))

(defn ^:export mount-preview-on-element [element file]
  (let [root-component (r/atom [:div])]
    (start-preview
     file
     root-component)
    (mount-root element root-component)))

(defn init! []
  (mount-diff-on-element
   (.getElementById js/document "app")
   "/example_xml/nice-example.xml"
   "/example_xml/nice-example-diff.xml"))
