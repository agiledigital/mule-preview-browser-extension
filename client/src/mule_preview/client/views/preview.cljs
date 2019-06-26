(ns mule-preview.client.views.preview
  (:require
   [mule-preview.client.transformer :refer [transform-xml-to-components]]
   [reagent.core :as r]
   [cljs.core.async :refer [<!]] 
   [cljs-http.client :as http]
   [tubax.core :refer [xml->clj]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn- handle-xml-fetch-success [response root-component]
  (let [parsed-xml (xml->clj (str response))
        transformed-components (transform-xml-to-components parsed-xml)]
    (reset! root-component transformed-components)))

(defn start-preview [url root-component]
  (go (let [response (<! (http/get url))]
        (handle-xml-fetch-success (:body response) root-component))))
  