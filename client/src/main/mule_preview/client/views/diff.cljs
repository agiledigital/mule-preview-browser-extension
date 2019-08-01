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

(def empty-file "<?xml version=\"1.0\" encoding=\"UTF-8\"?><mule></mule>")

(defn calculate-diff [content-a content-b content-root]
  (let [parsed-xml-a (xml->clj (or content-a empty-file))
        parsed-xml-b (xml->clj (or content-b empty-file))
        mast-a (xml->mast parsed-xml-a)
        mast-b (xml->mast parsed-xml-b)
        diff-output (diff mast-a mast-b)]
    (augment-mast-with-diff mast-a diff-output)))

(defn- handle-xml-fetch-success [response-a response-b root-component content-root]
  (let [augmented-mast (calculate-diff response-a response-b content-root)
        transformed-components (mast->react augmented-mast content-root)]
    (reset! root-component transformed-components)))

(defn start-diff-url [url-a url-b root-component content-root]
  (go (let [response-a (when url-a (<! (http/get url-a)))
            response-b (when url-b (<! (http/get url-b)))]
        (handle-xml-fetch-success
         (when response-a (str (:body response-a)))
         (when response-b  (str (:body response-b)))
         root-component
         content-root))))

(defn start-diff [content-a content-b root-component content-root]
  (handle-xml-fetch-success
   content-a
   content-b
   root-component
   content-root))