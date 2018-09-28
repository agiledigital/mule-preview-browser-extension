(ns mule-preview.components
  (:require 
    [reagent.core :as r]
    [mule-preview.mappings :refer [element-to-icon-map]]))

(defn- name-to-img-url [name]
 (str "img/icons/" (get element-to-icon-map name "generic-component-48x32.png")))

(defn mule-component [name description]
  [:div {:class "component"}
   [:img {:src (name-to-img-url name)}]
   [:div description]])

(defn mule-container [name children]
    [:div {:class "container"} 
      [:span {:class "container-title"} name]
       (into [] (concat [:div {:class "container-children"}] children))])