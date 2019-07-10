(defproject mule-preview "0.1.0-SNAPSHOT"
  :description "A react based library to render previews and diffs of Mule XML files"
  :url "https://github.com/noxharmonium/mule-preview"
  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/"}

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [reagent "0.8.1"]
                 [funcool/tubax "0.2.0"]
                 [cljs-http "0.1.46"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.18"]]

  :min-lein-version "2.5.0"
  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :resource-paths ["public"]

  :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]
                 :timeout 120000}

  :figwheel {:http-server-root "."
             :nrepl-port 7002
             :nrepl-middleware [cider.piggieback/wrap-cljs-repl]
             :css-dirs ["public/css"]}

  :cljsbuild {:builds {:app
                       {:source-paths ["src/mule_preview/client" "env/dev/cljs"]
                        :compiler
                        {:main "mule-preview.client.dev"
                         :output-to "public/js/dev.js"
                         :output-dir "public/js/dec"
                         :asset-path   "js/out"
                         :source-map true
                         :optimizations :none
                         :pretty-print true
                         :npm-deps false
                         :install-deps true
                         :infer-externs true
                         :foreign-libs [{:file "dist/index.bundle.js"
                                         :provides ["diffdom"]
                                         :global-exports {diffdom DiffDOM}}]}
                        :figwheel
                        {:on-jsload "mule-preview.client.core/mount-root"
                         :open-urls ["http://localhost:3449/index.html"]}}
                       :release
                       {:source-paths ["src/mule_preview/client" "env/prod/cljs"]
                        :compiler
                        {:output-to "public/js/release.js"
                         :output-dir "public/js/release"
                         :asset-path   "js/out"
                         :optimizations :advanced
                         :pretty-print false
                         :npm-deps false
                         :install-deps true
                         :infer-externs true
                         :foreign-libs [{:file "dist/index.bundle.js"
                                         :provides ["diffdom"]
                                         :global-exports {diffdom DiffDOM}}]}}}}

  :aliases {"package" ["do" "clean" ["cljsbuild" "once" "release"]]}

  :profiles {:dev {:source-paths ["src" "env/dev/clj"]
                   :dependencies [[binaryage/devtools "0.9.10"]
                                  [figwheel-sidecar "0.5.18"]
                                  [nrepl "0.6.0"]
                                  [cider/piggieback "0.4.0"]
                                  [org.clojure/tools.nrepl "0.2.13"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [cheshire "5.8.1"]]
                   :plugins [[cider/cider-nrepl "0.21.0"]]}})
