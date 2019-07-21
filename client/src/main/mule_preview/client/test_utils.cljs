(ns mule-preview.client.test-utils
  "Functions used by the Javascript tests to interact with ClojureScript"
  (:require
   [tubax.core :refer [xml->clj]]))

(def ^:export jsToClj #(js->clj % :keywordize-keys true))
(def ^:export cljToJs clj->js)
(def ^:export xmlToClj xml->clj)
