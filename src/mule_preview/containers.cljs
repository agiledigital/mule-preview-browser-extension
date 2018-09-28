(ns mule-preview.core)

(def containers
  ["scatter-gather", "async", "batch:job", "batch:step"
   "batch:commit", "ee:cache", "catch-exception-strategy"
   "choice", "choice-exception-strategy", "foreach"
   "composite-source", "enricher", "poll", "request-reply"
   "round-robin", "transactional", "until-successful"])
