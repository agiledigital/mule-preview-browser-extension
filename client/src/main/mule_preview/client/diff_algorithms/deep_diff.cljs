(ns mule-preview.client.diff-algorithms.deep-diff
  "Uses the deep-diff Javscript library to diff data structures.
   Not currently in use since the diffs were not very good to work with.
   See: https://github.com/flitbit/diff")

; Not sure why js->clj doesn't work but this does the trick
; https://stackoverflow.com/a/51439387
(defn clojurise [js-obj]
  (js->clj (-> js-obj js/JSON.stringify js/JSON.parse) :keywordize-keys true))

(defn diff [a b]
  (clojurise (.diff js/DeepDiff (clj->js a) (clj->js b))))