(ns mule-preview.tools.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [mule-preview.tools.image-extractor.copying :as c]
            [mule-preview.tools.light-theme-applier.applier :as a]
            [mule-preview.tools.mapping-generator.generation :as g]
            [mule-preview.tools.widget-type-extractor.extraction :as e])
  (:gen-class))

; Command line processing

(def cli-options
  [["-d" "--anypoint-dir DIR" "Anypoint Studio Directory"
    :validate [#(.isDirectory (io/file %)) "Must be a directory that exists"]]
   ["-o" "--output FOLDER" "Path where the generated mapping file will be written to"]
   ["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :update-fn inc]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["This is a tool for extracting extracting information from Mule plugins"
        "for use in the mule-preview client."
        ""
        "Usage: tools [options] action"
        ""
        "Options:"
        options-summary
        ""
        "Actions:"
        "  apply-light-theme     Extract images from the light theme plugin"
        "  extract-images        Extract images from plugins"
        "  extract-widget-types  Generate a list of possible widget types"
        "  generate-mappings     Generate mappings for a mule-preview client"
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn validate-args
  "Validate command line arguments. Either return a map indicating the program
  should exit (with a error message, and optional ok status), or a map
  indicating the action the program should take and the options provided."
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) ; help => exit OK with usage summary
      {:exit-message (usage summary) :ok? true}
      errors ; errors => exit with description of errors
      {:exit-message (error-msg errors)}
      (and (= 1 (count arguments))
           (#{"apply-light-theme"
              "extract-images"
              "extract-widget-types"
              "generate-mappings"} (first arguments))
           (:anypoint-dir options)
           (:output options))
      {:action (first arguments) :options options}
      :else ; failed custom validation => exit with usage summary
      {:exit-message (usage summary)})))

(defn exit [status msg]
  "Exits the program  immediatly with a message"
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [action options exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (let [anypoint-dir (:anypoint-dir options)
            output-dir (:output options)]
        (io/make-parents output-dir "dummy")
        (case action
          "apply-light-theme"    (a/apply-light-theme-from-anypoint-dir anypoint-dir output-dir)
          "extract-images"       (c/scan-directory-for-plugins anypoint-dir output-dir)
          "extract-widget-types" (e/scan-directory-for-widget-types anypoint-dir output-dir)
          "generate-mappings"    (g/scan-directory-for-plugins anypoint-dir output-dir))))))
