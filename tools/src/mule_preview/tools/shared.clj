(ns mule-preview.tools.shared
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
  #{:cloud-connector
    :cloud-connector-message-source
    :component
    :composite
    :connector
    :container
    :endpoint
    :filter
    :flow
    :global
    :global-cloud-connector
    :global-endpoint
    :global-filter
    :global-transformer
    :graphical-container
    :multi-source
    :nested
    :pattern
    :router
    :scope
    :transformer
    :wizard})

(def widget-category-map
  "A map of widget tags to categories. The category is used to determine what frame to put around the icon.
   This is only needed if the widget definition does not have a paletteCategory attribute.
   E.g filters have a purple frame"
  {:cloud-connector "org.mule.tooling.category.cloudconnectors"
   :cloud-connector-message-source "org.mule.tooling.category.cloudconnectors"
   :component "org.mule.tooling.category.core"
   :composite "org.mule.tooling.category.core"
   :connector "org.mule.tooling.category.endpoints"
   :container "org.mule.tooling.category.scopes"
   :endpoint "org.mule.tooling.category.endpoints"
   :filter "org.mule.tooling.category.filters"
   :flow "org.mule.tooling.category.flowControl"
   :global "org.mule.tooling.category.core"
   :global-cloud-connector "org.mule.tooling.category.cloudconnectors"
   :global-endpoint "org.mule.tooling.category.endpoints"
   :global-filter "org.mule.tooling.category.filters"
   :global-transformer "org.mule.tooling.category.transformers"
   :graphical-container "org.mule.tooling.category.scopes"
   :multi-source "org.mule.tooling.category.endpoints"
   :pattern "org.mule.tooling.category.core"
   :router "org.mule.tooling.category.flowControl"
   :scope "org.mule.tooling.category.scopes"
   :transformer "org.mule.tooling.category.transformers"
   :wizard "org.mule.tooling.category.scopes"})

(def mule-widget-xpaths
  "XPath expressions for finding extensions in the plugin.xml that indicate extractable widget definitions"
  (string/join "|" ["//plugin/extension[@point='org.mule.tooling.core.contribution']/externalContribution"
                    "//plugin/extension[@point='org.mule.tooling.core.contribution']/contribution"]))

(def mule-module-xpath
  "XPath expression for finding extensions in the plugin.xml that indicate module definitions"
  "//plugin/extension[@point='org.mule.tooling.module']/module/*")

(def jar-filename-regex
  "A regex used when searching for jar files to process"
  #"^.+(\/|\\).+\.jar$")

(def raw-filename-regex
  "A regex used when searching for raw plugins to process"
  #"^.+(\/|\\)plugin.xml$")

(def studio-feature-xml-regex
  "A regex used when searching for the Anypoint Studio feature file which contains the version"
  #"^.+features(\/|\\)org\.mule\.tooling\.studio_.*(\/|\\)feature.xml$")

(def light-theme-jar-regex
  "A regex used when searching the jar file that contains the light theme"
  #"^.+(\/|\\)org\.mule\.tooling\.ui\.theme\.light_.+\.jar$")

; Widget Scanning

(defn scan-for-files [dir patterns]
  "Recursivly walks the given directory and returns a sequence of 
   lazy sequences for each provided pattern"
  (let [s (file-seq (io/file dir))]
    (map (fn [pattern] (filter #(re-matches pattern (.getPath %)) s)) patterns)))

(defn xml-string-to-xml [xml-string]
  "Parses a string containing XML into a Clojure map"
  (xml/parse (java.io.ByteArrayInputStream. (.getBytes xml-string))))

(defn extract-widget-definition [xml-string]
  "Extracts widget definitions from a string containing plugin XML"
  (xp/$x mule-widget-xpaths xml-string))

(defn extract-module-definition [xml-string]
  "Extracts module definitions from a string containing plugin XML"
  (xp/$x mule-module-xpath xml-string))

(defn extract-subpaths-from-definition [definition]
  "Given a plugin definition map, extract the path to the widget definition"
  (-> definition :attrs :path))

; File manipulation

(defn concat-paths [base-file sub-path]
  "Concatinates the parent folder of the given base-file with the given sub-path"
  (io/file (.getParentFile (io/file base-file)) sub-path))

(defn filename [path]
  "Returns the filename of of a given path"
  (if (some? path) (.getName (io/file path)) nil))

; Reader functions

(defn zip-read-fn [jar-path sub-path]
  "A read function for use with process-plugins and jar files"
  (zu/read-file-from-zip jar-path sub-path))

(defn raw-read-fn [base-path sub-path]
  "A read function for use with process-plugins and raw (already extracted) plugins"
  (slurp (concat-paths base-path sub-path)))