(ns mule-preview.core
  (:require
   [mule-preview.transformer :refer [transform-xml-to-components]]
   [reagent.core :as r]
   [ajax.core :refer [GET]]
   [tubax.core :refer [xml->clj]]))

(def mule-components (r/atom [:div]))

(defn handle-xml-fetch-success [response]
  (let [parsed-xml (xml->clj (str response))
        transformed-components (transform-xml-to-components parsed-xml)]
    (reset! mule-components transformed-components)))

(defn handle-xml-fetch-error [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(GET "/example_xml/nice-example.xml" {:handler handle-xml-fetch-success
                                      :error-handler handle-xml-fetch-error})

;; -------------------------
;; Views

(defn home-page [] 
  [:div {:class "root-component"} @mule-components])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
