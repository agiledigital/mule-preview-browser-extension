(ns ^{:doc "Functions to convert Mule XML data structures to an intermediate MAST structure"}
 mule-preview.client.mast
  (:require
   [clojure.walk :refer [prewalk]]
   [mule-preview.client.diff-algorithms.diff-dom :refer [dom-to-node]]
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

(defn- create-mule-psuedo-container [content]
  {:type :container
   :tag-name "psuedo"
   :description ""
   :content content
   :attributes #{:horizontal}})

(defn- process-error-container [node tag-name attributes]
  (let [description (get-description node)
        content (node :content)
        {error-handlers true regular-components false}
        (group-by is-error-handler content)]
    {:type :error-container 
     :tag-name tag-name 
     :content [(create-mule-psuedo-container regular-components)
               (create-mule-psuedo-container error-handlers)]
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

(defn- prepare-path [path]
  (let [keyworded-path (map #(if (string? %) (keyword %) %) path)
        interposed (interpose :content keyworded-path)
        vector-path (vec interposed)]
    (vec (conj interposed :content))))

(defn insert [v i e] (vec (concat (take i v) [e] (drop i v))))

(defn vec-remove
  "remove elem in coll"
  [coll pos]
  (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))

(defn- add-element [mast route element]
  (let [index (last route)
        keyword-route (drop-last (prepare-path route))
        original-element (dom-to-node element)
        updated (update-in original-element [:attributes] #(conj % :added))]
    (update-in mast keyword-route #(insert % index updated))))

(defn- modify-element [mast route element name newValue]
  (let [index (last route)
        keyword-route (prepare-path route)
        original-element (get-in mast keyword-route)
        with-attributes (update-in original-element [:attributes] #(conj % :edited))
        with-description (assoc-in with-attributes [(keyword name)] newValue)]
    (assoc-in mast keyword-route with-description)))

(defn- remove-element [mast route]
  (let [index (last route)
        keyword-route (drop-last (prepare-path route))]
    (update-in mast keyword-route #(vec-remove (vec %) index))))

(defn- apply-patch [mast patch]
  (println patch)
  (let [{:keys [action route element newValue name]} patch]
    (case action
      "addElement" (add-element mast route element)
      "modifyAttribute" (modify-element mast route element name newValue)
      "removeElement" (remove-element mast route))))

(defn augment-mast-with-diff [mast diff]
    (reduce apply-patch mast diff))