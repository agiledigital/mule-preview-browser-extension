(ns mule-preview.components
  (:require 
    [reagent.core :as r]
    [clojure.string :refer [split]]
    [mule-preview.mappings :refer [element-to-icon-map]]))

(defn- normalise-name [name]
  (let [split-name (split name #":")
        first-component (first split-name)]
    (if (> (count split-name) 1)
       (str first-component ":*")
       first-component)))

(defn- name-to-img-url [name]
  (let [normalised-name (normalise-name name)]
    (str "img/icons/" (get element-to-icon-map normalised-name "generic-component-48x32.png"))))

(defn mule-component [name description]
  [:div {:class "component"}
   [:img {:src (name-to-img-url name)}]
   [:div description]])

(defn mule-container [name children]
    [:div {:class "container"} 
      [:span {:class "container-title"} name]
       (into [] (concat [:div {:class "container-children"}] children))])