(ns mule-preview.client.test-utils
  "Functions used by the Javascript tests to interact with ClojureScript"
  (:require
   [tubax.core :refer [xml->clj]]
   [reagent.core :as r]))

(def ^:export jsToClj #(js->clj % :keywordize-keys true))
(def ^:export cljToJs clj->js)
(def ^:export xmlToClj xml->clj)
(def ^:export makeAtom #(r/atom %))
(def ^:export reactifyComponent r/reactify-component)
(def ^:export makeSet #(set (map keyword (js->clj %))))
