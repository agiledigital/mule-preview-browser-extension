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


; [
;   {
;     "kind": "E",
;     "path": [
;       "content",
;       0,
;       "regular-content",
;       6,
;       "type"
;     ],
;     "lhs": "component",
;     "rhs": "container"
;   },
;   {
;     "kind": "E",
;     "path": [
;       "content",
;       0,
;       "regular-content",
;       6,
;       "tag-name"
;     ],
;     "lhs": "set-payload",
;     "rhs": "foreach"
;   },
;   {
;     "kind": "E",
;     "path": [
;       "content",
;       0,
;       "regular-content",
;       6,
;       "description"
;     ],
;     "lhs": "Server Noodles",
;     "rhs": "For Each"
;   },
;   {
;     "kind": "A",
;     "path": [
;       "content",
;       0,
;       "regular-content",
;       6,
;       "attributes"
;     ],
;     "index": 0,
;     "item": {
;       "kind": "N",
;       "rhs": "horizontal"
;     }
;   },
;   {
;     "kind": "N",
;     "path": [
;       "content",
;       0,
;       "regular-content",
;       6,
;       "content"
;     ],
;     "rhs": [
;       {
;         "type": "component",
;         "tag-name": "flow-ref",
;         "description": "example:/strain-overflow-noodle-requests",
;         "attributes": []
;       }
;     ]
;   },
;   {
;     "kind": "E",
;     "path": [
;       "content",
;       0,
;       "regular-content",
;       5,
;       "type"
;     ],
;     "lhs": "container",
;     "rhs": "component"
;   },
;   {
;     "kind": "E",
;     "path": [
;       "content",
;       0,
;       "regular-content",
;       5,
;       "tag-name"
;     ],
;     "lhs": "foreach",
;     "rhs": "expression-filter"
;   },
;   {
;     "kind": "E",
;     "path": [
;       "content",
;       0,
;       "regular-content",
;       5,
;       "description"
;     ],
;     "lhs": "For Each",
;     "rhs": "Filter overheated plasma purges"
;   },
;   {
;     "kind": "D",
;     "path": [
;       "content",
;       0,
;       "regular-content",
;       5,
;       "content"
;     ],
;     "lhs": [
;       {
;         "type": "component",
;         "tag-name": "flow-ref",
;         "description": "example:/strain-overflow-noodle-requests",
;         "attributes": []
;       }
;     ]
;   },
;   {
;     "kind": "A",
;     "path": [
;       "content",
;       0,
;       "regular-content",
;       5,
;       "attributes"
;     ],
;     "index": 0,
;     "item": {
;       "kind": "D",
;       "lhs": "horizontal"
;     }
;   },
;   {
;     "kind": "E",
;     "path": [
;       "content",
;       0,
;       "regular-content",
;       1,
;       "description"
;     ],
;     "lhs": "Set Z level to 9000",
;     "rhs": "Set Z level to 7000"
;   }
; ]

(defn- prepare-path [path]
  (let [parent-path (drop-last path)
        keyworded-path (map #(if (string? %) (keyword %) %) parent-path)
        vector-path (vec keyworded-path)]
  (conj vector-path :attributes)))

(defn- apply-patch [mast patch]
 (let [{:keys [kind path]} patch
       keyword-path (prepare-path path)]
   (println kind keyword-path)
   (case kind
     "N" mast ; TODO
     "D" mast ; TODO
     "E" (let [augmented (update-in mast keyword-path #(conj % :edited))] 
           (println "current" (get-in mast keyword-path))
           (println "after" (get-in augmented keyword-path))
           augmented)
     "A" mast ; TODO
     )))

(defn augment-mast-with-diff [mast diff]
  (reduce apply-patch mast diff))