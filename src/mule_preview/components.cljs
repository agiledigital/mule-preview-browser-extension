(ns mule-preview.components
  (:require 
    [reagent.core :as r]
    [clojure.string :refer [split]]
    [mule-preview.mappings :refer [element-to-icon-map]]))

(def default-component-image "generic-component-48x32.png")

(defn- normalise-name [name]
  (let [split-name (split name #":")
        first-component (first split-name)]
    (if (> (count split-name) 1)
       (str first-component ":*")
       first-component)))

(defn- name-to-img-url 
  [name default-value]
    (let [normalised-name (normalise-name name)
          filename (get element-to-icon-map normalised-name default-value)]
      (if-not (nil? filename)
        (str "img/icons/" filename)
        nil)))

(defn- image
  ([url] (image url ""))
  ([url class] 
    (if-not (nil? url) 
      [:img {:src url :class class}]
      nil)))

(defn- child-container [children]
  (into [] (concat [:div {:class "container-children"}] children)))

(def arrow
  (image "img/arrow-right-2x.png" "flow-arrow"))

(defn mule-component [name description]
  (let [img-url (name-to-img-url name default-component-image)]
    [:div {:class "component"}
      (image img-url "icon")
      [:div description]]))

(defn mule-container [name description children css-class]
  (let [img-url (name-to-img-url name nil)
        interposed-children (interpose arrow children)
        image-component (image img-url "icon container-image")
        child-container-component (child-container interposed-children)]
    [:div {:class ["container", css-class]} 
      [:div {:class "container-title"} description]
      [:div {:class "container-inner"}  
        image-component
        child-container-component]]))