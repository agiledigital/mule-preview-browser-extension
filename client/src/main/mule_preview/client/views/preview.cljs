(ns mule-preview.client.views.preview
  "Renders a page that displays a preview of a Mule XML file"
  (:require
   [mule-preview.client.react :refer [mast->react]]
   [mule-preview.client.mast :refer [xml->mast]]
   [mule-preview.client.views.common :refer [fetch-mappings]]
   [reagent.core :as r]
   [cljs.core.async :refer [<!]]
   [cljs-http.client :as http]
   [tubax.core :refer [xml->clj]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn- handle-xml-fetch-success [mappings response root-component content-root]
  (let [parsed-xml (xml->clj (str response))
        mast (xml->mast parsed-xml)
        transformed-components (mast->react mast mappings content-root)]
    (reset! root-component transformed-components)))

(defn start-preview-url [url root-component content-root]
  (go (let [mappings-response (<! (fetch-mappings content-root))
            mule-xml-response (<! (http/get url))]
        (handle-xml-fetch-success (:body mappings-response)
                                  (:body mule-xml-response)
                                  root-component
                                  content-root))))

(defn start-preview [content root-component content-root]
  (go (let [mappings-response (<! (fetch-mappings content-root))]
        (handle-xml-fetch-success (:body mappings-response)
                                  content
                                  root-component
                                  content-root))))