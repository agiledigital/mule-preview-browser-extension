(ns ^{:doc "Functions to convert Mule XML data structures to an intermediate MAST structure"}
 mule-preview.client.mast
  (:require
   [clojure.walk :refer [prewalk]]
   [mule-preview.client.mappings :refer [root-container horizontal-container-list
                                         vertical-container-list error-handler-component-list
                                         error-handler-container-list]]))

(defn- get-tag [node]
  (name (node :tag)))

(defn- is-error-handler [node]
  (let [tag (get-tag node)
        result (contains? error-handler-component-list tag)]
    result))

(defn- get-description [node]
  (let [doc-name (get-in node [:attributes :doc:name])
        name (get-in node [:attributes :name])]
    (or doc-name name)))

(defn- create-mule-component [node tag-name attributes]
  (let [description (get-description node)]
    {:type :component 
     :tag-name tag-name 
     :description description
     :attributes attributes}))

(defn- create-mule-container-component [node tag-name attributes]
  (let [description (get-description node)
        content (node :content)]
    {:type :container 
      :tag-name tag-name 
      :description description 
      :content content 
      :attributes attributes}))

(defn- process-error-container [node tag-name attributes]
  (let [description (get-description node)
        content (node :content)
        {error-handlers true regular-components false}
        (group-by is-error-handler content)]
    {:type :error-container 
     :tag-name tag-name 
     :description description 
     :regular-content regular-components 
     :error-content error-handlers
     :attributes attributes}))

(defn- transform-tag [node]
  (let [tag-name (get-tag node)
        is-root-container (= tag-name root-container)
        is-error-handler-container (contains? error-handler-container-list tag-name)
        is-error-handler-component (contains? error-handler-component-list tag-name)
        is-horizontal-container (contains? horizontal-container-list tag-name)
        is-vertical-container (contains? vertical-container-list tag-name)]
    (cond
      is-root-container (create-mule-container-component node tag-name #{:root :vertical})
      is-error-handler-container (process-error-container node tag-name #{:vertical})
      is-error-handler-component (create-mule-container-component node tag-name #{:error-handler})
      is-horizontal-container (create-mule-container-component node tag-name #{:horizontal})
      is-vertical-container (create-mule-container-component node tag-name #{:vertical})
      :else  (create-mule-component node tag-name #{}))))

(defn- transform-fn [node]
  (if (contains? node :tag)
    (let [transformed-tag (transform-tag node)]
      transformed-tag)
    node))

(defn xml->mast [xml]
  (prewalk transform-fn xml))