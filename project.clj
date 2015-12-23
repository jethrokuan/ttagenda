(defproject ttagenda "0.1.4-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [org.postgresql/postgresql "9.4-1206-jdbc42"]
                 [yesql "0.5.1"]
                 [compojure "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [environ "1.0.1"]
                 [clj-http "2.0.0"]
                 [org.clojure/data.json "0.2.6"]]
  :plugins [[lein-ring "0.9.7"]
            [lein-environ "1.0.1"]]
  :ring {:handler ttagenda.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
