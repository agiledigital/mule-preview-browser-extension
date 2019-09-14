(ns mule-preview.tools.widget-type-extractor.extraction
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

(def mule-widget-type-xpaths
  "XPath expressions for finding extensions in the plugin.xml that indicate extractable widget definitions"
  (string/join "|" ["//plugin/extension[@point='org.mule.tooling.core.contributionhandler']/contribution-handler"
                    "//plugin/extension[@point='org.mule.tooling.core.contributionhandler']/externalContribution-handler"]))

(defn extract-widget-type-definition [xml-string]
  "Extracts widget type definitions from a string containing plugin XML"
  (xp/$x mule-widget-type-xpaths xml-string))

(defn extract-widget-types-from-valid-definitions [definitions]
  (map #(-> % :attrs :name) (:definitions definitions)))

(defn process-plugins [file-list read-fn]
  "Processes a list of files and returns a merged map of widget definitions"
  (println "Scanning plugins...")
  (let
   [extracted-definitions (map #(assoc {}
                                       :file %
                                       :definitions (extract-widget-type-definition (read-fn % "plugin.xml"))) file-list)
    valid-definitions (filter #(not (empty? (:definitions %))) extracted-definitions)
    extracted-widget-types (map extract-widget-types-from-valid-definitions valid-definitions)]
    (flatten extracted-widget-types)))

(defn scan-directory-for-widget-types [root-dir output-dir]
  "Walks the given root dir looking for valid Mule widget plugin types,
   which it will process into a merged map of widget type definitions
   and write them to a JSON file"
  (let [output-file (io/file output-dir "widget-type.json")
        [raw-plugins jars] (scan-for-files root-dir [raw-filename-regex jar-filename-regex])
        jars-with-plugins (filter #(zu/zip-contains-file % "plugin.xml") jars)
        jar-scan-output (process-plugins jars-with-plugins zip-read-fn)
        raw-scan-output (process-plugins raw-plugins raw-read-fn)]
    (cc/generate-stream (concat jar-scan-output raw-scan-output) (io/writer output-file))
    (println "Successfully wrote widget types file to [" output-file "]")))
