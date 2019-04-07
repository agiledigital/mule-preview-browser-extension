(ns mule-preview.client.macros
  (:require [cheshire.core :as cc]))

(defmacro get-data [jsonfile]
  (cc/parse-string (slurp jsonfile) true))