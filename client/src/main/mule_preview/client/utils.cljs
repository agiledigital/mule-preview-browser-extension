(ns mule-preview.client.utils
  "Helper functions to provide functionallity not provided by the Clojure standard library"
  (:require
   [clojure.walk :refer [prewalk]]))

(defn insert
  "Inserts an element into a vector at an index
   There is probably better ways to do this but this works for now."
  [v i e] (vec (concat (take i v) [e] (drop i v))))

(defn vec-remove
  "Removes an element at an index from a vector"
  [coll pos]
  (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))

(defn map-values [f map]
  "Transforms the values in a map using the provided function f"
  (into {} (for [[k v] map] [k (f v)])))

(defn remove-location [map]
  "Removes any location key in a parsed XML map."
  (prewalk #(if (map? %) (dissoc % :location) %) map))
