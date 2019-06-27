(ns mule-preview.client.views.diff
  (:require
   [mule-preview.client.react :refer [mast->react]]
   [mule-preview.client.mast :refer [xml->mast]]
   [reagent.core :as r]
   [cljs.core.async :refer [<!]]
   [cljs-http.client :as http]
   [tubax.core :refer [xml->clj]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn- handle-xml-fetch-success [response-a response-b root-component]
  (let [parsed-xml-a (xml->clj (str response-a))
        parsed-xml-b (xml->clj (str response-b))
        mast-a (xml->mast parsed-xml-a)
        mast-b (xml->mast parsed-xml-b)
        diff (.diff js/DeepDiff (clj->js mast-a) (clj->js mast-b))
        ; _ (cljs.pprint/pprint mast-b)
        _ (.dir js/console diff)
        transformed-components (mast->react mast-a)]
    (reset! root-component transformed-components)))

(defn start-diff [url-a url-b root-component]
  (go (let [response-a (<! (http/get url-a))
            response-b (<! (http/get url-b))]
        (handle-xml-fetch-success 
          (:body response-a) 
          (:body response-b) 
          root-component))))