(ns mule-preview.client.components
  "The react components that render the Mule preview"
  (:require
   [reagent.core :as r]
   [clojure.string :refer [split replace]]
   [mule-preview.client.mappings :refer [element-to-icon-map]]
   [lambdaisland.uri :refer [join]]
   [re-com.core :refer [popover-content-wrapper popover-anchor-wrapper]])
  (:require-macros [mule-preview.client.macros :as m]))

(def default-component-mapping {:image "UnknownNode-48x32.png"})
(def default-category-image "org.mule.tooling.ui.modules.core.miscellaneous.large.png")

(defn- pluralise
  "Pluralise a given string value"
  [string]
  (str string "s"))

(defn map-kv [m f]
  (reduce-kv #(assoc %1 (f %2) %3) {} m))

; (defn- data-prefixerise
;   "Prefixes all the keys in a map with 'data-' to align with HTML standards"
;   [m]
;   (into {} (for [[k v] m] [(str "data-" (name k)) v])))

(defn- data-prefixerise
  "Prefixes all the keys in a map with 'data-' to align with HTML standards"
  [m]
  (map-kv m #(str "data-" (name %))))

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
  (when name (let [normalized-name (replace name #":" "_")]
               (str "mule-" normalized-name))))

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

(defn delta-text [name [previous current]]
  (cond
    (and (some? previous) (some? current)) [:div
                                            {:key name} (str name ": ")
                                            [:span {:class ["previous"]} previous]
                                            " > "
                                            [:span {:class ["current"]} current]]
    (and (some? previous) (nil? current)) [:div
                                           {:key name}
                                           [:span {:class ["previous"]} (str name ": ") previous]]
    (and (nil? previous) (some? current)) [:div
                                           {:key name}
                                           [:span {:class ["current"]} (str name ": ") current]]))

(defn tooltip-item [{:keys [name delta]}]
  (if (#{"content-hash"} name)
    [:div
     "Content Changed"]
    (delta-text name delta)))

(defn tooltip-inner [change-record]
  [:div (->>
         change-record
         (remove #(#{"hash" "description"} (:name %)))
         (map tooltip-item))])

(defn tooltip [change-record]
  [popover-content-wrapper
   :title "Changes"
   :body (tooltip-inner change-record)])

(defn mule-component [{:keys [name description css-class content-root location change-record]}]
  (let [showing? (r/atom false)]
    (fn [] (let [img-url (name-to-img-url name false default-component-mapping)
                 category-url (name-to-category-url name default-category-image)]
             [popover-anchor-wrapper
              :position :below-right
              :showing? showing?
              :popover (tooltip change-record)
              :anchor [:div {:class ["component-container"]}
                       [:div
                        (merge {:class ["component" name css-class]}
                               (when change-record {:on-mouse-over (m/handler-fn (reset! showing? true))
                                                    :on-mouse-out  (m/handler-fn (reset! showing? false))})
                               (data-prefixerise location))
                        (image category-url "category-frame" content-root)
                        (image img-url "icon" content-root)
                        [:div {:class "label"} description]]]]))))

(defn mule-container [{:keys [name description children css-class content-root location change-record]}]
  (let [showing? (r/atom false)]
    (fn []
      (let [generated-css-class (name-to-css-class name)
            img-url (name-to-img-url name (some? children) nil)
            category-url (name-to-category-url name default-category-image)
            interposed-children (interpose (arrow content-root) children)
            child-container-component (child-container interposed-children)]
        [popover-anchor-wrapper
         :position :below-right
         :showing? showing?
         :popover (tooltip change-record)
         :anchor
         [:div (merge {:class ["container" generated-css-class css-class]}
                      (when change-record {:on-mouse-over (m/handler-fn (reset! showing? true))
                                           :on-mouse-out  (m/handler-fn (reset! showing? false))})
                      (data-prefixerise location))
          [:div {:class "container-title"} description]
          [:div {:class "container-inner"}
           [:div {:class "icon-container"}
            (image category-url "category-frame" content-root)
            (image img-url "icon container-image" content-root)]
           child-container-component]]]))))

; Exports for testing with Jest
(def ^:export MuleComponent (r/reactify-component [mule-component]))
(def ^:export MuleContainer (r/reactify-component [mule-container]))