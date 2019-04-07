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

; Widget Scanning

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

(defn extract-subpaths-from-definition [definition]
  "Given a plugin definition map, extract the path to the widget definition"
  (-> definition :attrs :path))

; File manipulation

(defn concat-paths [base-file sub-path]
  "Concatinates the parent folder of the given base-file with the given sub-path"
  (io/file (.getParentFile (io/file base-file)) sub-path))

(defn filename [path]
  "Returns the filename of of a given path"
  (.getName (io/file path)))

; Reader functions

(defn zip-read-fn [jar-path sub-path]
  "A read function for use with process-plugins and jar files"
  (zu/read-file-from-zip jar-path sub-path))

(defn raw-read-fn [base-path sub-path]
  "A read function for use with process-plugins and raw (already extracted) plugins"
  (slurp (concat-paths base-path sub-path)))