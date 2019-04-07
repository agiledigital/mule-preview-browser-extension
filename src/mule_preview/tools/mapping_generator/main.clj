(ns mule-preview.tools.mapping-generator.main
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [mule-preview.tools.mapping-generator.generation :as g])
  (:gen-class))

; Command line processing

(def cli-options
  [["-d" "--anypoint-dir DIR" "Anypoint Studio Directory"
    :validate [#(.isDirectory (io/file %)) "Must be a directory that exists"]]
   ["-o" "--output FILE" "Path where the generated mapping file will be written to"
    :default "public/mappings.json"]
   ["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :update-fn inc]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["This is a tool for extracting mapping JSON files for Mule widgets."
        ""
        "Usage: mapping-generator [options]"
        ""
        "Options:"
        options-summary
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
  (let [{:keys [options errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) ; help => exit OK with usage summary
      {:exit-message (usage summary) :ok? true}
      errors ; errors => exit with description of errors
      {:exit-message (error-msg errors)}
      (:anypoint-dir options)
      {:options options} ; Pass through if valid
      :else ; failed custom validation => exit with usage summary
      {:exit-message (usage summary)})))

(defn exit [status msg]
  "Exits the program  immediatly with a message"
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (g/scan-directory-for-plugins (:anypoint-dir options) (:output options)))))
