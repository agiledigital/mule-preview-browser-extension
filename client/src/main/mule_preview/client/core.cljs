(ns mule-preview.client.core
  "The entry point to the web application"
  (:require
   [reagent.core :as r]
   [tubax.core :refer [xml->clj]]
   [mule-preview.client.views.preview :refer [start-preview start-preview-url]]
   [mule-preview.client.views.diff :refer [start-diff start-diff-url]]))

(def root-component (r/atom [:div]))

(defn view [root-component]
  [:div {:class "root-component"} @root-component])

(defn mount-root [element root-component]
  (r/render [(partial view root-component)] element))

(defn ^:export mount-url-diff-on-element [element file-a file-b]
  (let [root-component (r/atom [:div])]
    (start-diff-url file-a
                    file-b
                    root-component)
    (mount-root element root-component)))

(defn ^:export mount-url-preview-on-element [element file]
  (let [root-component (r/atom [:div])]
    (start-preview-url
     file
     root-component)
    (mount-root element root-component)))

(defn ^:export mount-diff-on-element [element content-a content-b]
  (let [root-component (r/atom [:div])]
    (start-diff content-a
                content-b
                root-component)
    (mount-root element root-component)))

(defn ^:export mount-preview-on-element [element content]
  (let [root-component (r/atom [:div])]
    (start-preview
     content
     root-component)
    (mount-root element root-component)))

(defn init! []
  (mount-url-diff-on-element
   (.getElementById js/document "app")
   "/example_xml/nice-example.xml"
   "/example_xml/nice-example-diff.xml"))
