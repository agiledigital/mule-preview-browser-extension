(ns mule-preview.tools.mapping-generator.generation
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.xml :as xml]
            [cheshire.core :as cc]
            [mule-preview.tools.zip-utils :as zu]
            [clj-xpath.core :as xp])
  (:import [java.util.zip ZipInputStream]
           [java.io FileOutputStream])
  (:gen-class))

; Constants

(def mule-widget-tags
  "A list of tags names that indicate an extractable Mule widget
   The main list was extracted from Mule schema files but some were added manually"
  #{:connector
    :endpoint
    :multi-source
    :wizard
    :global
    :pattern
    :scope
    :global-filter
    :global-transformer
    :global-cloud-connector
    :global-endpoint
    :filter
    :transformer
    :widget
    :flow
    :router
    :cloud-connector
    :nested})

(def mule-widget-xpaths
  "XPath expressions for finding extensions in the plugin.xml that indicate extractable widget definitions"
  (string/join "|" ["//plugin/extension[@point='org.mule.tooling.core.contribution']/externalContribution"
                    "//plugin/extension[@point='org.mule.tooling.core.contribution']/contribution"]))

(def jar-filename-regex
  "A regex used when searching for jar files to process"
  #".*\.jar")

(def raw-filename-regex
  "A regex used when searching for raw plugins to process"
  #"plugin.xml")

; Mapping generation

(defn scan-for-files [dir pattern]
  "Recursivly walks the given directory and returns files matching the given pattern"
  (filter
   #(re-matches pattern (.getName %))
   (file-seq (io/file dir))))

(defn xml-string-to-xml [xml-string]
  "Parses a string containing XML into a Clojure map"
  (xml/parse (java.io.ByteArrayInputStream. (.getBytes xml-string))))

(defn extract-widget-definition [xml-string]
  "Extracts widget definitions from a string containing plugin XML"
  (xp/$x mule-widget-xpaths xml-string))

(defn element-name [prefix, widget]
  "Determines the element name for the mapping file
   Elements from the core namespace are not prefixed in Mule"
  (string/join ":"
               (filter some? [(if (= prefix "core") nil prefix)
                              (-> widget :attrs :localId)])))

(defn extract-mapping-from-plugin [plugin-path sub-path read-fn]
  "Given a path to a plugin, extract the mapping for a particular sub path of the plugin
   Uses the given read-fn to extract the XML data"
  (let [target-file-contents (read-fn plugin-path sub-path)
        parsed-xml (xml-string-to-xml target-file-contents)
        prefix (-> parsed-xml :attrs :prefix)
        widgets (:content parsed-xml)
        filtered-widgets (filter #(mule-widget-tags (:tag %)) widgets)]
    (map #(assoc {}
                 (element-name prefix %)
                 {:image (-> % :attrs :image)
                  :category (-> % :tag)}) filtered-widgets)))

(defn extract-mappings-from-plugin [plugin-path sub-paths read-fn]
  "Given a path to a plugin, extract the mapping for a list of sub paths
   Uses the given read-fn to extract the XML data"
  (println "Extracting mappings from valid plugin [" (.getAbsolutePath plugin-path) "]")
  (flatten (map #(extract-mapping-from-plugin plugin-path % read-fn) sub-paths)))

(defn extract-subpaths-from-definition [definition]
  "Given a plugin definition map, extract the path to the widget definition"
  (-> definition :attrs :path))

(defn concat-paths [base-file sub-path]
  "Concatinates the parent folder of the given base-file with the given sub-path"
  (io/file (.getParentFile (io/file base-file)) sub-path))

(defn process-plugins [file-list read-fn]
  "Processes a list of files and returns a merged map of widget definitions"
  (println "Scanning plugins...")
  (let
   [extracted-definitions (map #(assoc {}
                                       :file %
                                       :definitions (extract-widget-definition (read-fn % "plugin.xml"))) file-list)
    valid-definitions (filter #(not (empty? (:definitions %))) extracted-definitions)
    extracted-mapping (map #(extract-mappings-from-plugin
                             (-> % :file)
                             (map extract-subpaths-from-definition (-> % :definitions))
                             read-fn) valid-definitions)]
    (into {} (flatten extracted-mapping))))

(defn zip-read-fn [jar-path sub-path]
  "A read function for use with process-plugins and jar files"
  (zu/read-file-from-zip jar-path sub-path))

(defn raw-read-fn [base-path sub-path]
  "A read function for use with process-plugins and raw (already extracted) plugins"
  (slurp (concat-paths base-path sub-path)))

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
