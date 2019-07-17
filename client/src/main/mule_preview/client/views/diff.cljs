(ns mule-preview.client.views.diff
  "Renders a page that shows a diff between two Mule XML files"
  (:require
   [mule-preview.client.react :refer [mast->react]]
   [mule-preview.client.mast :refer [xml->mast]]
   [mule-preview.client.transformers.apply_patch :refer [augment-mast-with-diff]]
   [mule-preview.client.diff-algorithms.diff-dom :refer [diff]]
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
        diff-output (diff mast-a mast-b)
        augmented-mast (augment-mast-with-diff mast-a diff-output)
        transformed-components (mast->react augmented-mast)]
    (reset! root-component transformed-components)))

(defn start-diff-url [url-a url-b root-component]
  (go (let [response-a (<! (http/get url-a))
            response-b (<! (http/get url-b))]
        (handle-xml-fetch-success
         (:body response-a)
         (:body response-b)
         root-component))))

(defn start-diff [content-a content-b root-component]
  (handle-xml-fetch-success
   content-a
   content-b
   root-component))