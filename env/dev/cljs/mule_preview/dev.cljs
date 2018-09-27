(ns ^:figwheel-no-load mule-preview.dev
  (:require
    [mule-preview.core :as core]
    [devtools.core :as devtools]))


(enable-console-print!)

(devtools/install!)

(core/init!)
