(ns mule-preview.client.diff-algorithms.deep-diff)

; Not sure why js->clj doesn't work but this does the trick
; https://stackoverflow.com/a/51439387
(defn clojurise [js-obj]
  (js->clj (-> js-obj js/JSON.stringify js/JSON.parse) :keywordize-keys true))

(defn diff [a b]
  (clojurise (.diff js/DeepDiff (clj->js a) (clj->js b))))