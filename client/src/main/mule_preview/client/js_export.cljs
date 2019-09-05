(ns mule-preview.client.js-export
  "Entry point for dependant Javascript projects.
   Exports the function names with camel case to fit Javascript conventions"
  (:require
   [mule-preview.client.core :refer [mount-url-diff-on-element
                                     mount-url-preview-on-element
                                     mount-diff-on-element
                                     mount-preview-on-element]]))

(def ^:export mountUrlDiffOnElement mount-url-diff-on-element)
(def ^:export mountUrlPreviewOnElement mount-url-preview-on-element)
(def ^:export mountDiffOnElement mount-diff-on-element)
(def ^:export mountPreviewOnElement mount-preview-on-element)

