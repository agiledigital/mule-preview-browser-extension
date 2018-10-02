(ns mule-preview.transformer
  (:require 
    [clojure.walk :refer [prewalk]]
    [mule-preview.mappings :refer [container-list error-handler-list]]
    [mule-preview.components :refer [mule-component mule-container mule-error-handler]]))

(defn- get-description [node]
 (let [doc-name (get-in node [:attributes :doc:name])
       name (get-in node [:attributes :name])]
   (or doc-name name)))

(defn- transform-tag [node]
  (let [tag-name (name (node :tag))
        description (get-description node)
        is-error-handler (contains? error-handler-list tag-name)
        is-container (contains? container-list tag-name)]
    (cond
      is-error-handler (mule-error-handler description (node :content))
      is-container (mule-container description (node :content))
      :else  (mule-component tag-name description))))

(defn- transform-fn [node]
  (if (contains? node :tag)
    (let [tag-name (node :tag)
          transformed-tag (transform-tag node)]
      transformed-tag)
    node))

(defn transform-xml-to-components [xml]
    (prewalk transform-fn xml))
