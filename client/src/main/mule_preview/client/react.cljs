(ns mule-preview.client.react
  "Functions to convert MAST data structures to a structure suitable for reagent (react)"
  (:require
   [clojure.walk :refer [postwalk]]
   [mule-preview.client.mappings :refer [root-container horizontal-container-list
                                         vertical-container-list error-handler-component-list
                                         error-handler-container-list]]
   [mule-preview.client.components :refer [mule-component mule-container]]))

(defn- labels-to-css [labels]
  (clojure.string/join " " (map name labels)))

(defn- create-mule-component [node content-root]
  (let [{:keys [tag-name description labels location change-record]} node]
    [(mule-component {:name tag-name
                      :description description
                      :css-class (labels-to-css labels)
                      :content-root content-root
                      :location location
                      :change-record change-record})]))

(defn- create-mule-container-component [node content-root]
  (let [{:keys [tag-name description content labels location change-record]} node]
    [(mule-container {:name tag-name
                      :description description
                      :children content
                      :css-class (labels-to-css labels)
                      :content-root content-root
                      :location location
                      :change-record change-record})]))

(defn- transform-tag [node content-root]
  (let [type (:type node)]
    (case type
      :error-container (create-mule-container-component node content-root)
      :container (create-mule-container-component node content-root)
      :component (create-mule-component node content-root))))

(defn- inner-transform-fn [content-root node]
  (if (contains? node :type)
    (let [transformed-tag (transform-tag node content-root)]
      transformed-tag)
    node))

(defn- outer-transform-fn [content-root node]
  node)

(defn mast->react [xml content-root]
  ; Needs to be a post walk so that the elements furthest from the root
  ; get transformed first, before they are wrapped up in functions and can't
  ; be traversed anymore
  (postwalk (partial inner-transform-fn content-root) xml))
