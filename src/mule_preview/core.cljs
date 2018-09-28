(ns mule-preview.core
  (:require
   [mule-preview.transformer :refer [transform-xml-to-components]]
   [reagent.core :as r]
   [ajax.core :refer [GET]]
   [tubax.core :refer [xml->clj]]))

(def mule-components (r/atom [:div]))

(defn handle-xml-fetch-success [response]
  (let [parsed-xml (xml->clj (str response))
        transformed-components (first (transform-xml-to-components parsed-xml))]
    (prn transformed-components)
    (reset! mule-components transformed-components)))

(defn handle-xml-fetch-error [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(GET "/example_xml/simple-example.xml" {:handler handle-xml-fetch-success
                                      :error-handler handle-xml-fetch-error})

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Here why don't you just have some Mules?"]
   @mule-components])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
