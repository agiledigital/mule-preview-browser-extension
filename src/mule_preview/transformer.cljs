(ns mule-preview.transformer
  (:require 
    [clojure.walk :refer [prewalk]]
    [mule-preview.mappings :refer [container-list]]
    [mule-preview.components :refer [mule-component mule-container]]))

(defn- get-description [node]
 (let [doc-name (get-in node [:attributes :doc:name])
       name (get-in node [:attributes :name])]
   (or doc-name name)))

(defn- transform-tag [node]
  (let [tag-name (name (node :tag))
        description (get-description node)]
    (if (contains? container-list tag-name)
        (mule-container description (node :content))
        (mule-component tag-name description))))

(defn- transform-fn [node]
  (if (contains? node :tag)
    (let [tag-name (node :tag)]
      (cond
        (= :mule tag-name) (node :content)
        :else (transform-tag node)))
    node))

(defn transform-xml-to-components [xml]
    (prewalk transform-fn xml))
