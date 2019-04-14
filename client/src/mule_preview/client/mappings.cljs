(ns mule-preview.client.mappings
  (:require-macros [mule-preview.client.macros :as m]))

(def root-container "mule")

(def error-handler-container-list #{"flow"})

(def vertical-container-list
  #{"scatter-gather" "choice" "composite-source" "round-robin"})

(def horizontal-container-list
  #{"flow" "sub-flow" "async" "batch:job"
    "batch:step" "batch:commit" "ee:cache" "foreach"
    "enricher" "poll" "request-reply" "transactional"
    "until-successful" "when" "otherwise" "processor-chain"})

(def error-handler-component-list
  #{"catch-exception-strategy" "choice-exception-strategy"
    "exception-strategy" "rollback-exception-strategy"})

(def element-to-icon-map (m/get-data "src/mule_preview/client/mappings.json"))