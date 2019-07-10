(ns mule-preview.client.prod
  (:require
   [mule-preview.client.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))
