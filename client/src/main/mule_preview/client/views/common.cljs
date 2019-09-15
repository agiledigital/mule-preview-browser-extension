(ns mule-preview.client.views.common
  "Common functions shared by views"
  (:require
   [cljs.core.async :refer [<!]]
   [cljs-http.client :as http]
   [lambdaisland.uri :refer [join]]))

(defn fetch-mappings [content-root]
  (let [url (join content-root "mappings.json")]
    (http/get url)))