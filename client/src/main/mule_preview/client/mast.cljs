(ns mule-preview.client.mast
  "Functions to convert Mule XML data structures to an intermediate MAST structure"
  (:require
   [clojure.walk :refer [prewalk]]
   [clojure.set :refer [union]]
   [clojure.string :refer [starts-with? split replace capitalize]]
   [mule-preview.client.utils :refer [remove-location]]
   [mule-preview.client.mappings :refer [root-container horizontal-container-list
                                         vertical-container-list error-handler-component-list
                                         error-handler-container-list munit-container-list]]))

(defn- get-tag [node]
  (name (node :tag)))

(defn- get-munit-title [tag-name]
  (let [munit-flow-name (last (split tag-name #":"))
        spaced (replace munit-flow-name #"-" " ")]
    (capitalize spaced)))

(defn- is-error-handler [node]
  (let [tag (get-tag node)
        result (contains? error-handler-component-list tag)]
    result))

(defn- is-munit-mock-component [node]
  (let [tag (get-tag node)
        result (starts-with? tag "mock:")]
    result))

(defn- get-description [node]
  (let [doc-name (get-in node [:attributes :doc:name])
        name (get-in node [:attributes :name])]
    (or doc-name name)))

(defn- create-mule-component [node tag-name labels]
  (let [description (get-description node)
        content (node :content)
        attributes (:attributes node)]
    {:type :component
     :tag-name tag-name
     :description (or description tag-name)
     :hash (hash (:attributes node))
     :content-hash (hash (remove-location content))
     :labels labels
     :location (:location node)
     :attributes attributes}))

(defn- create-mule-container-component [node tag-name labels]
  (let [description (get-description node)
        content (node :content)
        attributes (:attributes node)]
    {:type :container
     :tag-name tag-name
     :description description
     :hash (hash (:attributes node))
     :content content
     :labels labels
     :location (:location node)
     :attributes attributes}))

(defn-
  create-mule-psuedo-container
  ([content]
   (create-mule-psuedo-container content #{} ""))
  ([content extra-labels]
   (create-mule-psuedo-container content extra-labels ""))
  ([content extra-labels title]
   {:type :container
    :tag-name "psuedo"
    :description title
    :content content
    :labels (union #{:horizontal} extra-labels)}))

(defn- process-error-container [node tag-name labels]
  (let [description (get-description node)
        content (node :content)
        {error-handlers true regular-components false}
        (group-by is-error-handler content)
        attributes (:attributes node)]
    {:type :error-container
     :tag-name tag-name
     :description description
     :attributes attributes
     :content [(create-mule-psuedo-container regular-components #{:top})
               (create-mule-psuedo-container error-handlers #{:bottom})]
     :labels labels
     :location (:location node)}))

(defn- process-munit-container [node tag-name labels]
  (let [description (get-description node)
        content (node :content)
        {mocks true regular-components false}
        (group-by is-munit-mock-component content)
        title (get-munit-title tag-name)
        is-split-flow (= tag-name "munit:test")
        attributes (:attributes node)]
    {:type :munit-container
     :tag-name tag-name
     :description description
     :attributes attributes
     :content [(create-mule-psuedo-container mocks #{:top} (if is-split-flow "Setup" title))
               (create-mule-psuedo-container regular-components #{:bottom} (if is-split-flow "Test" ""))]
     :labels labels
     :location (:location node)}))

(defn- transform-tag [node]
  (let [tag-name (get-tag node)
        is-root-container (= tag-name root-container)
        is-munit-container (contains? munit-container-list tag-name)
        is-error-handler-container (contains? error-handler-container-list tag-name)
        is-error-handler-component (contains? error-handler-component-list tag-name)
        is-horizontal-container (contains? horizontal-container-list tag-name)
        is-vertical-container (contains? vertical-container-list tag-name)]
    (cond
      is-root-container (create-mule-container-component node tag-name #{:root :vertical})
      is-munit-container (process-munit-container node tag-name #{:vertical :munit})
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
