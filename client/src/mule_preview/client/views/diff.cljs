(ns mule-preview.client.views.diff
  (:require
   [mule-preview.client.transformer :refer [transform-xml-to-components]]
   [reagent.core :as r]
   [cljs.core.async :refer [<!]] 
   [cljs-http.client :as http]
   [tubax.core :refer [xml->clj]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn- handle-xml-fetch-success [response-a response-b root-component]
  (let [parsed-xml-a (xml->clj (str response-a))
        parsed-xml-b (xml->clj (str response-b))
        ; TODO: Actually do a diff
        transformed-components (transform-xml-to-components parsed-xml-a)]
    (reset! root-component transformed-components)))

(defn start-diff [url-a url-b root-component]
  (go (let [response-a (<! (http/get url-a))
            response-b (<! (http/get url-b))]
        (handle-xml-fetch-success 
          (:body response-a) 
          (:body response-b) 
          root-component))))