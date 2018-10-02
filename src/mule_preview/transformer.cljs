(ns mule-preview.transformer
  (:require 
    [clojure.walk :refer [prewalk]]
    [mule-preview.mappings :refer [root-container horizontal-container-list 
                                   vertical-container-list error-handler-list]]
    [mule-preview.components :refer [mule-component mule-container]]))

(defn- get-description [node]
 (let [doc-name (get-in node [:attributes :doc:name])
       name (get-in node [:attributes :name])]
   (or doc-name name)))

(defn- create-mule-component [node tag-name]
   (let [description (get-description node)]
    (mule-component tag-name description)))

(defn- create-mule-container-component [node tag-name css-class]
  (let [description (get-description node)
        content (node :content)]
    (mule-container tag-name description content css-class)))

(defn- transform-tag [node]
  (let [tag-name (name (node :tag))
        is-root-container (= tag-name root-container)
        is-error-handler (contains? error-handler-list tag-name)
        is-horizontal-container (contains? horizontal-container-list tag-name)
        is-vertical-container (contains? vertical-container-list tag-name)]
    (cond
      is-root-container (create-mule-container-component node tag-name "vertical root")
      is-error-handler (create-mule-container-component node tag-name "error-handler")
      is-horizontal-container (create-mule-container-component node tag-name "horizontal")
      is-vertical-container (create-mule-container-component node tag-name "vertical")
      :else  (create-mule-component node tag-name))))

(defn- transform-fn [node]
  (if (contains? node :tag)
    (let [tag-name (node :tag)
          transformed-tag (transform-tag node)]
      transformed-tag)
    node))

(defn transform-xml-to-components [xml]
    (prewalk transform-fn xml))
