(ns ^{:doc "Functions to convert MAST data structures to a structure suitable for reagent (react)"}
 mule-preview.client.react
  (:require
   [clojure.walk :refer [prewalk]]
   [mule-preview.client.mappings :refer [root-container horizontal-container-list
                                         vertical-container-list error-handler-component-list
                                         error-handler-container-list]]
   [mule-preview.client.components :refer [mule-component mule-container]]))

(defn- attributes-to-css [attributes]
  (clojure.string/join " " (map name attributes)))

(defn- create-mule-component [node]
  (let [{:keys [tag-name description attributes]} node]
    (mule-component tag-name description (attributes-to-css attributes))))

(defn- create-mule-container-component [node]
  (let [{:keys [tag-name description content attributes]} node]
    (mule-container tag-name description content (attributes-to-css attributes))))

(defn- process-error-container [node]
  (let [{:keys [tag-name description attributes regular-content error-content]} node
        wrapped-content [(mule-container "psuedo" "" regular-content "horizontal")
                         (mule-container "psuedo" "" error-content "horizontal")]]
    (mule-container tag-name description wrapped-content (attributes-to-css attributes))))

(defn- transform-tag [node]
  (let [type (:type node)]
    (case type
      :error-container (process-error-container node)
      :container (create-mule-container-component node)
      :component (create-mule-component node))))

(defn- transform-fn [node]
  (if (contains? node :type)
    (let [transformed-tag (transform-tag node)]
      transformed-tag)
    node))

(defn mast->react [xml]
  (prewalk transform-fn xml))
