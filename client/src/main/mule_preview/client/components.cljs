(ns mule-preview.client.components
  "The react components that render the Mule preview"
  (:require
   [reagent.core :as r]
   [clojure.string :refer [split]]
   [mule-preview.client.mappings :refer [element-to-icon-map]]
   [lambdaisland.uri :refer [join]]))

(def default-component-mapping {:image "UnknownNode-48x32.png"})
(def default-category-image "org.mule.tooling.ui.modules.core.miscellaneous.large.png")

(defn- pluralise
  "Pluralise a given string value"
  [string]
  (str string "s"))

(defn- normalise-name [name]
  (let [split-name (split name #":")
        first-component (first split-name)]
    (if (> (count split-name) 1)
      (str first-component ":*")
      first-component)))

(defn- name-to-category-url [name default-image]
  (let [mapping (get element-to-icon-map (keyword name))
        category (:category mapping)
        filename (if (some? category) (str category ".large.png") default-image)]
    (if-not (nil? filename)
      (str "img/icons/" filename)
      nil)))

(defn- name-to-img-url [name is-nested default-value]
  (let [mapping (get element-to-icon-map (keyword name) default-value)
        regular-filename (:image mapping)
        filename (if is-nested (get mapping :nested-image regular-filename) regular-filename)]
    (if-not (nil? filename)
      (str "img/icons/" filename)
      nil)))

(defn- name-to-css-class [name]
  (str "mule-" name))

(defn- image
  ([url content-root] (image url "" content-root))
  ([url class content-root]
   (if-not (nil? url)
     [:img {:src (str (join content-root url)) :class class}]
     nil)))

(defn- child-container [children]
  (into [] (concat [:div {:class "container-children"}] children)))

(defn- arrow [content-root]
  (image "img/arrow-right-2x.png" "flow-arrow" content-root))

(defn mule-component [name description css-class content-root]
  (let [img-url (name-to-img-url name false default-component-mapping)
        category-url (name-to-category-url name default-category-image)]
    [:div {:class ["component" name css-class]}
     (image category-url "category-frame" content-root)
     (image img-url "icon" content-root)
     [:div {:class "label"} description]]))

(defn mule-container [name description children css-class content-root]
  (let [generated-css-class (name-to-css-class name)
        img-url (name-to-img-url name (some? children) nil)
        category-url (name-to-category-url name default-category-image)
        interposed-children (interpose (arrow content-root) children)
        child-container-component (child-container interposed-children)]
    [:div {:class ["container" generated-css-class css-class]}
     [:div {:class "container-title"} description]
     [:div {:class "container-inner"}
      [:div {:class "icon-container"}
       (image category-url "category-frame" content-root)
       (image img-url "icon container-image" content-root)]
      child-container-component]]))