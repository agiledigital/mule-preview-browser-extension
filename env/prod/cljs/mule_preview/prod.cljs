(ns mule-preview.prod
  (:require
    [mule-preview.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
