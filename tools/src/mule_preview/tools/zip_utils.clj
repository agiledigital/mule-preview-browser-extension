(ns mule-preview.tools.zip-utils
  "Utility functions to help read from zip files"
  (:require [clojure.java.io :as io]
            [clojure.string :as string])
  (:import [java.util.zip ZipInputStream ZipFile]))


(defn get-next-zip-entry
  "Wraps getNextEntry to throw an exception on nil so that read-file-from-zip doesn't get stuck in an infinite loop"
  [zip-stream]
  (let [entry (.getNextEntry zip-stream)]
    (if (nil? entry) (throw (AssertionError. "End of zip file reached")) entry)))

(defn read-file-from-zip [zip-file filename]
  "Reads the given filename (full path to file in zip) from the given zip file and returns it as a string
   Will throw exception if filename is not found so ensure you know it exists beforehand"
  ; (println "Reading [" filename "] from [" (.getName zip-file) "]")
  (with-open [zip-stream (ZipInputStream. (io/input-stream zip-file))]
    (some
     #(when (= (.getName %) filename) %)
     (repeatedly #(get-next-zip-entry zip-stream)))
    (string/join "\n" (line-seq (io/reader zip-stream)))))

(defn copy-file-from-zip [zip-file filename target-file]
  "Reads the given filename (full path to file in zip) from the given zip file and writes it to a given file
   Will throw exception if filename is not found so ensure you know it exists beforehand"
  (println "Copying [" filename "] from [" (.getName zip-file) "] to [" target-file "]")
  (with-open [zip-stream (ZipInputStream. (io/input-stream zip-file))
              output-stream (io/output-stream target-file)]
    (some
     #(when (= (.getName %) filename) %)
     (repeatedly #(get-next-zip-entry zip-stream)))
    (io/copy zip-stream output-stream)))

(defn zip-contains-file [zip-file filename]
  "Given a zip file and a filename (full path to file in zip) return if the filename exists in the zip file or not"
  (let [zip-file-definition (ZipFile. zip-file)
        entries (enumeration-seq (.entries zip-file-definition))
        names (map #(.getName %) entries)]
    ; (println "Checking that " filename "is in" zip-file)
    (some #{filename} names)))

(defn list-files-from-zip-matching [zip-file pattern]
  (let [zip-file-reader (ZipFile. zip-file)
        entries (enumeration-seq (.entries zip-file-reader))
        names (map #(.getName %) entries)]
    (filter #(re-matches pattern %) names)))