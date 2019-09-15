(ns mule-preview.tools.light-theme-applier.applier
  (:require [clojure.java.io :as io]
            [mule-preview.tools.zip-utils :as zu])
  (:import [java.util.zip ZipInputStream]
           [java.io FileOutputStream])
  (:use [mule-preview.tools.shared])
  (:gen-class))


(defn find-light-theme [root-dir]
  (let [[light-theme-jars] (scan-for-files root-dir [light-theme-jar-regex])]
    (first light-theme-jars)))

(defn copy-file-to-output-dir [zip-file path output-dir]
  (let [output-file (io/file output-dir (filename path))]
    (zu/copy-file-from-zip zip-file path output-file)))

(defn apply-light-theme-from-anypoint-dir [root-dir output-dir]
  "Locates the light theme plugin from an Anypoint Studio installation and applies
  it to the images that were extracted from widgets"
  (let [light-theme-plugin (find-light-theme root-dir)
        files-to-apply (zu/list-files-from-zip-matching light-theme-plugin #".+\.png")
        files-excluding-small-folder (filter #(not (re-matches #".+\/small\/.+" %)) files-to-apply)]
    (doall (map #(copy-file-to-output-dir light-theme-plugin % output-dir) files-excluding-small-folder))
    (println "Successfully applied the light theme to [" output-dir "]")))
