(ns mule-preview.client.macros
  "Utility functions that transform the code at compile time"
  (:require [cheshire.core :as cc]))

(defmacro get-data [jsonfile]
  "Pulls a JSON file into a data structure at compile time it can be embedded in the final bundle"
  (cc/parse-string (slurp jsonfile) true))

; https://github.com/reagent-project/reagent/wiki/Beware-Event-Handlers-Returning-False
(defmacro handler-fn
  ([& body]
   `(fn [~'event] ~@body nil)))  ;; force return nil