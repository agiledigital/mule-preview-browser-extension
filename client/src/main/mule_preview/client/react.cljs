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

(defn- create-mule-component [node mappings content-root]
  (let [{:keys [tag-name description labels location change-record]} node]
    [(mule-component {:name tag-name
                      :description description
                      :css-class (labels-to-css labels)
                      :content-root content-root
                      :location location
                      :change-record change-record
                      :labels labels
                      :mappings mappings})]))

(defn- create-mule-container-component [node mappings content-root]
  (let [{:keys [tag-name description content labels location change-record]} node]
    [(mule-container {:name tag-name
                      :description description
                      :children content
                      :css-class (labels-to-css labels)
                      :content-root content-root
                      :location location
                      :change-record change-record
                      :labels labels
                      :mappings mappings})]))

(defn- transform-tag [mappings content-root node]
  (let [type (:type node)]
    (case type
      :munit-container (create-mule-container-component node mappings content-root)
      :error-container (create-mule-container-component node mappings content-root)
      :container (create-mule-container-component node mappings content-root)
      :component (create-mule-component node mappings content-root))))

(defn- inner-transform-fn [mappings content-root node]
  (if (contains? node :type)
    (let [transformed-tag (transform-tag mappings content-root node)]
      transformed-tag)
    node))

(defn- outer-transform-fn [content-root node]
  node)

(defn mast->react [xml mappings content-root]
  ; Needs to be a post walk so that the elements furthest from the root
  ; get transformed first, before they are wrapped up in functions and can't
  ; be traversed anymore
  (postwalk (partial inner-transform-fn mappings content-root) xml))
