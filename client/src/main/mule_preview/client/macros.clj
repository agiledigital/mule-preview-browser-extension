(ns mule-preview.client.macros
  "Utility functions that transform the code at compile time")

; https://github.com/reagent-project/reagent/wiki/Beware-Event-Handlers-Returning-False
(defmacro handler-fn
  ([& body]
   `(fn [~'event] ~@body nil)))  ;; force return nil