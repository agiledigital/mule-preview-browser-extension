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

(defn extract-anypoint-version [feature-xml-file]
  "Finds the org.mule.tooling.studio feature XML file
   which seems (to be a reasonable way of finding the version of Anypoint studio"
  (when feature-xml-file 
    (let [feature-xml-contents (slurp feature-xml-file)
          parsed-xml (xml-string-to-xml feature-xml-contents)]
      (-> parsed-xml :attrs :version))))

(defn element-name [prefix name]
  "Determines the element name for the mapping file
   Elements from the core namespace are not prefixed in Mule"
  (string/join ":"
               (filter some? [(if (= prefix "core") nil prefix)
                              name])))

(defn element-names [prefix widget]
  "Determines the element name for the mapping file
   Elements from the core namespace are not prefixed in Mule"
  (let [possible-names [:inboundLocalName :localId :outboundLocalName :localName :muleLocalName]
        tag (:tag widget)
        possible-names (map #(-> widget :attrs %) possible-names)
        endpoint-names (if (= tag :endpoint) ["outbound-endpoint" "inbound-endpoint"] [])
        all-names (concat possible-names endpoint-names)]
    (map #(element-name prefix %) all-names)))

(defn associate-with-element-names [prefix widget]
  "A single widget may define several names. For example, endpoints can have an inbound and outbound name.
   This method associates all the possible names a widget can have, with the widget definition."
  (let [element-names (element-names prefix widget)]
    (map #(assoc {} :element-name % :widget widget) element-names)))

(defn extract-category-from-widget [widget]
  "There are multiple ways that a category can be defined. Some widgets have the paletteCategory attribute,
   others have the category attribute and some have neither, so the category must be inferred by it's tag name"
  (let [tag (:tag widget)
        attrs (:attrs widget)
        palette-category (:paletteCategory attrs)
        category (:category attrs)
        mapped-category ((keyword tag) widget-category-map)]
    (or palette-category category mapped-category)))

(defn is-valid-widget [widget]
  "Determines whether the specified widget XML element represents a widget or not"
  (let [tag (:tag widget)
        attrs (:attrs widget)]
    (mule-widget-tags tag)))

(defn create-element-from-widget [widget]
  "Creates the actual mapping used by the client. It combines information from several places."
  (let [inner (:widget widget)
        image  (filename (-> inner :attrs :image))
        element-name (:element-name widget)
        tag (:tag inner)
        is-nested (#{:nested} tag)
        category (extract-category-from-widget inner)
        image-map (if is-nested {:nested-image image} {:image image})
        category-map (if (some? category) {:category category} {})]
    {element-name (merge image-map category-map)}))

(defn extract-mapping-from-elements [xml-elements prefix]
  "Takes a list of candidate widget definitions and converts it to the mapping used by the client."
  (let [filtered-widgets (filter is-valid-widget xml-elements)
        element-names-map (flatten (map #(associate-with-element-names prefix %) filtered-widgets))]
    (map create-element-from-widget element-names-map)))

(defn extract-mapping-from-subpath [plugin-path sub-path read-fn]
  "Given a path to a plugin, extract the mapping for a particular sub path of the plugin
   Uses the given read-fn to extract the XML data"
  (let [target-file-contents (read-fn plugin-path sub-path)
        parsed-xml (xml-string-to-xml target-file-contents)
        prefix (-> parsed-xml :attrs :prefix)
        widgets (:content parsed-xml)]
    (extract-mapping-from-elements widgets prefix)))

(defn extract-mappings-from-subpaths [plugin-path sub-paths read-fn]
  "Given a path to a plugin, extract the mapping for a list of sub paths
   Uses the given read-fn to extract the XML data"
  (println "Extracting mappings from valid plugin [" (.getAbsolutePath plugin-path) "]")
  (flatten (map #(extract-mapping-from-subpath plugin-path % read-fn) sub-paths)))

(defn extract-mappings-from-valid-definitions [definition read-fn]
  "Iterates over each path specified in the widget definition and processes it to extract the mapping."
  (extract-mappings-from-subpaths
   (:file definition)
   (map extract-subpaths-from-definition (:definitions definition))
   read-fn))

(defn extract-mappings-from-plugin-modules [plugin-xml]
  "Some widget definitions are in the plugin.xml file itself. This method generates the mapping for these widgets."
  (let [modules (extract-module-definition (:plugin-xml plugin-xml))]
    (extract-mapping-from-elements modules "core")))

(defn extract-widget-definitions-from-plugin [plugin-xml]
  "Extracts the widget definitions from a plugin XML file that are later used to create the mappings."
  (let [widget-definitions (extract-widget-definition (:plugin-xml plugin-xml))]
    {:file (:file plugin-xml)
     :definitions widget-definitions}))

(defn process-plugins [file-list read-fn]
  "Processes a list of files and returns a merged map of widget definitions"
  (println "Scanning plugins...")
  (let
   [plugin-xmls (map  #(assoc {} :file % :plugin-xml (read-fn % "plugin.xml")) file-list)
    extracted-widget-definitions (map extract-widget-definitions-from-plugin plugin-xmls)
    valid-definitions (filter #(not (empty? (:definitions %))) extracted-widget-definitions)
    extracted-mappings (map #(extract-mappings-from-valid-definitions % read-fn) valid-definitions)
    extracted-module-mappings (map extract-mappings-from-plugin-modules plugin-xmls)
    all-mappings (concat extracted-module-mappings extracted-mappings)]
    (apply (partial merge-with merge) (flatten all-mappings))))

(defn scan-directory-for-plugins [root-dir output-dir]
  "Walks the given root dir looking for valid Mule widget plugins, which it will process into a merged map of widget definitions
   and write them to a JSON file"
  (let [output-file (io/file output-dir "mappings.json")
        [raw-plugins jars studio-feature-xmls] (scan-for-files root-dir
                                                               [raw-filename-regex jar-filename-regex studio-feature-xml-regex])
        anypoint-version (extract-anypoint-version (first studio-feature-xmls))
        jars-with-plugins (filter #(zu/zip-contains-file % "plugin.xml") jars)
        jar-scan-output (process-plugins jars-with-plugins zip-read-fn)
        raw-scan-output (process-plugins raw-plugins raw-read-fn)]
    (cc/generate-stream
     {:mapping-version 1 :anypoint-version anypoint-version :mappings (merge jar-scan-output raw-scan-output)}
     (io/writer output-file))
    (println "Successfully wrote mappings file to [" output-file "]")))
