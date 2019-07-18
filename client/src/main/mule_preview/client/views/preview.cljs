(ns mule-preview.client.views.preview
  "Renders a page that displays a preview of a Mule XML file"
  (:require
   [mule-preview.client.react :refer [mast->react]]
   [mule-preview.client.mast :refer [xml->mast]]
   [reagent.core :as r]
   [cljs.core.async :refer [<!]]
   [cljs-http.client :as http]
   [tubax.core :refer [xml->clj]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn- handle-xml-fetch-success [response root-component content-root]
  (let [parsed-xml (xml->clj (str response))
        mast (xml->mast parsed-xml)
        transformed-components (mast->react mast content-root)]
    (reset! root-component transformed-components)))

(defn start-preview-url [url root-component content-root]
  (go (let [response (<! (http/get url))]
        (handle-xml-fetch-success (:body response) root-component content-root))))

(defn start-preview [content root-component content-root]
  (handle-xml-fetch-success content root-component content-root))