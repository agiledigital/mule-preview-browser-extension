(ns mule-preview.client.components
  "The react components that render the Mule preview"
  (:require
   [reagent.core :as r]
   [react-dom :as react-dom]
   [clojure.string :refer [split replace]]
   [lambdaisland.uri :refer [join]]
   ["react-popper" :refer [Manager Reference Popper]])
  (:require-macros [mule-preview.client.macros :as m]))

(def default-component-mapping {:image "UnknownNode-48x32.png"})
(def default-category-image "org.mule.tooling.ui.modules.core.miscellaneous.large.png")

(defn- pluralise
  "Pluralise a given string value"
  [string]
  (str string "s"))

(defn map-kv [m f]
  (reduce-kv #(assoc %1 (f %2) %3) {} m))

(defn- normalise-name [name]
  (let [split-name (split name #":")
        first-component (first split-name)]
    (if (> (count split-name) 1)
      (str first-component ":*")
      first-component)))

(defn- name-to-category-url [name default-image mappings]
  (let [element-to-icon-map (:mappings mappings)
        mapping (get element-to-icon-map (keyword name))
        category (:category mapping)
        filename (if (some? category) (str category ".large.png") default-image)]
    (if-not (nil? filename)
      (str "img/icons/" filename)
      nil)))

(defn- name-to-img-url [name is-nested default-value mappings]
  (let [element-to-icon-map (:mappings mappings)
        mapping (get element-to-icon-map (keyword name) default-value)
        regular-filename (:image mapping)
        filename (if is-nested (get mapping :nested-image regular-filename) regular-filename)]
    (if-not (nil? filename)
      (str "img/icons/" filename)
      nil)))

(defn- labels-to-diff-icon-url [labels]
  (cond
    (:added labels) "img/plus.svg"
    (:edited labels) "img/is-not-equal-to.svg"
    (:removed labels) "img/minus.svg"
    :else nil))

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
                                           [:span {:class ["previous"]}
                                            (str  name ": ")
                                            previous
                                            " (Attribute Removed)"]]
    (and (nil? previous) (some? current)) [:div
                                           {:key name}
                                           [:span {:class ["current"]}
                                            (str  name ": ")
                                            current
                                            " (Attribute Added)"]]))

(defn tooltip-item [{:keys [name delta]}]
  (if (#{"content-hash"} name)
    [:div
     {:key name}
     "Content Changed"]
    (delta-text name delta)))

(defn tooltip-edited [change-record]
  [:div (->>
         change-record
         (remove #(#{"hash" "description"} (:name %)))
         (map tooltip-item))])


(defn tooltip-added []
  [:div "Element Added"])

(defn tooltip-removed []
  [:div "Element Removed"])

(defn tooltip [change-record labels location placement]
  [:div {:class ["mp-popover" placement]}
   [:h3 {:class "mp-popover-title"}
    (str "Line " (:line location) ", Column " (:column location))]
   [:div {:class "mp-popover-content"}
    (cond
      (:added labels) (tooltip-added)
      (:removed labels) (tooltip-removed)
      (:edited labels) (tooltip-edited change-record)
      :else nil)]])

(defn popper [change-record labels location showing-atom anchor-el]
  (when @showing-atom (react-dom/createPortal
                       (r/as-element [:div {:class "mp-popover-root"}
                                      [:> Popper {:placement "auto" :reference-element @anchor-el}
                                       (fn [props]
                                         (let [{:keys [ref style placement]} (js->clj props :keywordize-keys true)]
                                           (r/as-element [:div {:ref ref :style style :data-placement placement :class placement}
                                                          (tooltip change-record labels location placement)])))]])
                       (.-body js/document))))

(defn mule-component-inner [{:keys [name description css-class content-root location change-record showing-atom labels mappings]}]
  (let [anchor-el (clojure.core/atom nil)
        img-url (name-to-img-url name false default-component-mapping mappings)
        diff-icon-url (labels-to-diff-icon-url labels)
        category-url (name-to-category-url name default-category-image mappings)
        should-show-tooltip (or change-record (:added labels) (:removed labels))]
    (fn []
      [:div {:ref #(reset! anchor-el %)}
       [:div {:class ["component-container" css-class]
              :on-mouse-over (m/handler-fn (reset! showing-atom should-show-tooltip))
              :on-mouse-out  (m/handler-fn (reset! showing-atom false))}
        (when diff-icon-url (image diff-icon-url "diff-icon" content-root))
        [:div
         {:class ["component" name]}
         (image category-url "category-frame" content-root)
         (image img-url "icon" content-root)
         [:div {:class "label"} description]]]
       (popper change-record labels location showing-atom anchor-el)])))

(defn mule-component [props]
  (let [showing-atom (r/atom false)]
    (fn [] (mule-component-inner (assoc props :showing-atom showing-atom)))))

(defn mule-container-inner [{:keys [name description children css-class content-root location change-record showing-atom labels mappings]}]
  (let [anchor-el (clojure.core/atom nil)
        generated-css-class (name-to-css-class name)
        img-url (name-to-img-url name (some? children) nil mappings)
        category-url (name-to-category-url name default-category-image mappings)
        interposed-children (interpose (arrow content-root) children)
        child-container-component (child-container interposed-children)
        should-show-tooltip (or change-record (:added labels) (:removed labels))]
    (fn []
      [:div {:ref #(reset! anchor-el %)}
       [:div {:class ["container" generated-css-class css-class]
              :on-mouse-over (m/handler-fn (reset! showing-atom should-show-tooltip))
              :on-mouse-out  (m/handler-fn (reset! showing-atom false))}
        [:div {:class "container-title"} description]
        [:div {:class "container-inner"}
         [:div {:class "icon-container"}
          (image category-url "category-frame" content-root)
          (image img-url "icon container-image" content-root)]
         child-container-component]]
       (popper change-record labels location showing-atom anchor-el)])))

(defn mule-container [props]
  (let [showing-atom (r/atom false)]
    (fn [] (mule-container-inner (assoc props :showing-atom showing-atom)))))

; Exports for testing with Jest
(def ^:export MuleComponent (r/reactify-component mule-component-inner))
(def ^:export MuleContainer (r/reactify-component mule-container-inner))