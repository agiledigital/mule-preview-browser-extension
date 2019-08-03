(defproject tools "1.0.7"
  :description "Command line tools for processing Mule XML files"
  :url "https://github.com/NoxHarmonium/mule-preview"
  :license {:name "Apache License"
            :url  "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.cli "0.4.2"]
                 [com.github.kyleburton/clj-xpath "1.4.11"]
                 [cheshire "5.8.1"]]
  :main ^:skip-aot mule-preview.tools.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
