(ns mule-preview.client.diff-algorithms.diff-dom
  "Uses the diffDOM Javascript library to diff data structures
   https://github.com/fiduswriter/diffDOM"
  (:require
   [clojure.walk :refer [prewalk]]
   ["diff-dom" :refer (DiffDOM)]))

(defn- node-to-dom [node]
  (if (:type node)
    (let [{:keys [tag-name content type attributes location labels]} node
          other-attributes (dissoc node :type-tag :content :type :labels :attributes :location)]
      {:nodeName tag-name
       :childNodes content
       :attributes (merge attributes other-attributes)
       :original-type type
       :original-labels labels
       :location location})
    node))

(defn dom-to-node [node]
  (if (:nodeName node)
    (let [{:keys [nodeName childNodes attributes original-type original-labels location]} node]
      (merge {:tag-name nodeName
              :content childNodes
              :type (keyword original-type)
              :labels original-labels
              :location location} attributes))
    node))

(defn mast->dom [mast]
  (prewalk node-to-dom mast))

(defn dom->mast [dom]
  (prewalk dom-to-node dom))

(defn clojurise [js-obj]
  (let [round-trip (-> js-obj js/JSON.stringify js/JSON.parse)]
    (js->clj round-trip :keywordize-keys true)))

(defn diff [a b]
  (let [dd (new DiffDOM)
        dom-a (mast->dom a)
        dom-b (mast->dom b)]
    (clojurise (.diff dd (clj->js dom-a) (clj->js dom-b)))))