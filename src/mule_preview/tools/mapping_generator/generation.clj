(ns mule-preview.tools.mapping-generator.generation
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.xml :as xml]
            [cheshire.core :as cc]
            [mule-preview.tools.zip-utils :as zu]
            [clj-xpath.core :as xp])
  (:import [java.util.zip ZipInputStream]
           [java.io FileOutputStream])
  (:use [mule-preview.tools.shared])
  (:gen-class))

(defn element-name [prefix widget attribute-name]
  "Determines the element name for the mapping file
   Elements from the core namespace are not prefixed in Mule"
  (string/join ":"
               (filter some? [(if (= prefix "core") nil prefix)
                              (-> widget :attrs attribute-name)])))

(defn element-names [prefix widget]
  "Determines the element name for the mapping file
   Elements from the core namespace are not prefixed in Mule"
  (let [possible-names [:inboundLocalName :localId :outboundLocalName :localName]]
    (map #(element-name prefix widget %) possible-names)))

(defn associate-with-element-names [prefix widget]
  (let [element-names (element-names prefix widget)]
    (map #(assoc {} :element-name % :widget widget) element-names)))

(defn extract-category-from-widget [widget]
  (let [tag (:tag widget)
        attrs (:attrs widget)
        mapped-category ((keyword tag) widget-category-map)]
    (get attrs :paletteCategory mapped-category)))

(defn is-valid-widget [widget]
  (let [tag (:tag widget)
        attrs (:attrs widget)]
    (mule-widget-tags tag)))

(defn create-element-from-widget [widget]
  (let [inner (:widget widget)
        image  (filename (-> inner :attrs :image))
        element-name (:element-name widget)
        tag (:tag inner)
        is-nested (#{:nested} tag)
        category (extract-category-from-widget inner)
        image-map (if is-nested {:nested-image image} {:image image})
        category-map (if (some? category) {:category category} {})]
    {element-name (merge image-map category-map)}))

(defn extract-mapping-from-subpath [plugin-path sub-path read-fn]
  "Given a path to a plugin, extract the mapping for a particular sub path of the plugin
   Uses the given read-fn to extract the XML data"
  (let [target-file-contents (read-fn plugin-path sub-path)
        parsed-xml (xml-string-to-xml target-file-contents)
        prefix (-> parsed-xml :attrs :prefix)
        widgets (:content parsed-xml)
        filtered-widgets (filter is-valid-widget widgets)
        element-names-map (flatten (map #(associate-with-element-names prefix %) filtered-widgets))]
    (map create-element-from-widget element-names-map)))

(defn extract-mappings-from-subpaths [plugin-path sub-paths read-fn]
  "Given a path to a plugin, extract the mapping for a list of sub paths
   Uses the given read-fn to extract the XML data"
  (println "Extracting mappings from valid plugin [" (.getAbsolutePath plugin-path) "]")
  (flatten (map #(extract-mapping-from-subpath plugin-path % read-fn) sub-paths)))

(defn extract-mappings-from-valid-definitions [definition read-fn]
  (extract-mappings-from-subpaths
   (:file definition)
   (map extract-subpaths-from-definition (:definitions definition))
   read-fn))

(defn process-plugins [file-list read-fn]
  "Processes a list of files and returns a merged map of widget definitions"
  (println "Scanning plugins...")
  (let
   [extracted-definitions (map #(assoc {}
                                       :file %
                                       :definitions (extract-widget-definition (read-fn % "plugin.xml"))) file-list)
    valid-definitions (filter #(not (empty? (:definitions %))) extracted-definitions)
    extracted-mapping (map #(extract-mappings-from-valid-definitions % read-fn) valid-definitions)]
    (apply (partial merge-with merge) (flatten extracted-mapping))))

(defn scan-directory-for-plugins [root-dir output-file]
  "Walks the given root dir looking for valid Mule widget plugins, which it will process into a merged map of widget definitions
and write them to a JSON file"
  (let [raw-plugins (scan-for-files root-dir raw-filename-regex)
        jars (scan-for-files root-dir jar-filename-regex)
        jars-with-plugins (filter #(zu/zip-contains-file % "plugin.xml") jars)
        jar-scan-output (process-plugins jars-with-plugins zip-read-fn)
        raw-scan-output (process-plugins raw-plugins raw-read-fn)]
    (cc/generate-stream (merge jar-scan-output raw-scan-output) (io/writer output-file))
    (println "Successfully wrote mappings file to [" output-file "]")))
