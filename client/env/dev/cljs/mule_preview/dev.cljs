(ns ^:figwheel-no-load mule-preview.client.dev
  (:require
   [mule-preview.client.core :as core]
   [devtools.core :as devtools]))


(enable-console-print!)

(devtools/install!)

(core/init!)
