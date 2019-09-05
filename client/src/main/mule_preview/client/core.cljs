(ns mule-preview.client.core
  "The entry point to the web application"
  (:require
   [reagent.core :as r]
   [mule-preview.client.views.preview :refer [start-preview start-preview-url]]
   [mule-preview.client.views.diff :refer [start-diff start-diff-url]]))

(defn view [root-component]
  [:div {:class "mp root-component"} @root-component])

(defn mount-root [element root-component]
  (r/render [(partial view root-component)] element))

(defn ^:export mount-url-diff-on-element [element file-a file-b content-root]
  (let [root-component (r/atom [:div])]
    (start-diff-url file-a
                    file-b
                    root-component
                    content-root)
    (mount-root element root-component)))

(defn ^:export mount-url-preview-on-element [element file content-root]
  (let [root-component (r/atom [:div])]
    (start-preview-url
     file
     root-component
     content-root)
    (mount-root element root-component)))

(defn ^:export mount-diff-on-element [element content-a content-b content-root]
  (let [root-component (r/atom [:div])]
    (start-diff content-a
                content-b
                root-component
                content-root)
    (mount-root element root-component)))

(defn ^:export mount-preview-on-element [element content content-root]
  (let [root-component (r/atom [:div])]
    (start-preview
     content
     root-component
     content-root)
    (mount-root element root-component)))

(defn ^:dev/after-load init! []
  (mount-url-diff-on-element
   (.getElementById js/document "app")
   "/example_xml/nice-example.xml"
   "/example_xml/nice-example-diff.xml"
   "."))
