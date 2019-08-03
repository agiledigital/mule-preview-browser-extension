(ns mule-preview.client.transformers.apply_patch
  "Functions that take the output of diffDOM and transform a MAST data structure to reflect the changes in the diff."
  (:require
   [clojure.walk :refer [prewalk]]
   [mule-preview.client.utils :refer [insert vec-remove map-values]]
   [mule-preview.client.diff-algorithms.diff-dom :refer [dom->mast]]
   [mule-preview.client.mappings :refer [root-container horizontal-container-list
                                         vertical-container-list error-handler-component-list
                                         error-handler-container-list]]))

(defn- prepare-path [path]
  (let [keyworded-path (map #(if (string? %) (keyword %) %) path)
        interposed (interpose :content keyworded-path)
        vector-path (vec interposed)]
    (vec (conj interposed :content))))

(defn- insert-removal [element-route original-element removal-map]
  (let [index (last element-route)
        parent (drop-last element-route)]
    (update removal-map parent #(conj % [index original-element]))))

(defn- shift-element [insert-index removed-index]
  (if (< insert-index removed-index) (inc removed-index) removed-index))

(defn- shift-removal [route removal-map]
  (let [index (last route)
        parent (drop-last route)]
    (update removal-map parent
            (fn [coll] (map-values #(shift-element index %) coll)))))

(defn- add-element [mast removal-map route element]
  (let [index (last route)
        keyword-route (drop-last (prepare-path route))
        original-element (dom->mast element)
        updated (update-in original-element [:labels] #(set (conj % :added)))]
    [(update-in mast keyword-route #(insert % index updated)) (shift-removal route removal-map)]))

(defn- modify-element [mast removal-map route element name oldValue newValue]
  (let [index (last route)
        keyword-route (prepare-path route)
        original-element (get-in mast keyword-route)
        with-labels (update-in original-element [:labels] #(set (conj % :edited)))
        with-description (assoc-in with-labels [(keyword name)] newValue)
        with-change-record (update-in with-description [:change-record]
                                      #(conj % {:name name :delta [oldValue newValue]}))]
    [(assoc-in mast keyword-route with-change-record) removal-map]))

(defn- remove-element [mast removal-map route]
  (let [index (last route)
        element-route (prepare-path route)
        keyword-route (drop-last element-route)
        original-element (get-in mast element-route)]
    [(update-in mast keyword-route #(vec-remove (vec %) index))
     (insert-removal element-route original-element removal-map)]))

(defn- replace-element [mast removal-map route newValue]
  (let [[mast removal-map] (remove-element mast removal-map route)]
    (add-element mast removal-map route newValue)))

(defn- apply-patch [in patch]
  (let [[mast removal-map] in
        {:keys [action route element value oldValue newValue name]} patch]
    (if (empty? route)
      [mast removal-map] ; Should not patch the root element at this time
      (case action
        "addElement" (add-element mast removal-map route element)
        "replaceElement" (replace-element mast removal-map route newValue)
        "addAttribute" (modify-element mast removal-map route element name nil value)
        "modifyAttribute" (modify-element mast removal-map route element name oldValue newValue)
        "removeAttribute" (modify-element mast removal-map route element name value nil)
        "removeElement" (remove-element mast removal-map route)))))

(defn- process-removal [path mast [index element]]
  (let [keyword-route path
        updated (update-in element [:labels] #(set (conj % :removed)))]
    (update-in mast keyword-route #(insert % index updated))))

(defn- process-removal-path [mast [path removals]]
  (reduce (partial process-removal path) mast removals))

(defn- apply-removal-map [mast removal-map]
  (let [mast-with-removals (reduce process-removal-path mast (reverse removal-map))]
    mast-with-removals))

(defn augment-mast-with-diff [mast diff]
  (let [removal-map {}
        [mast removal-map] (reduce apply-patch [mast removal-map] diff)]
    (apply-removal-map mast removal-map)))
