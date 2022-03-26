(defproject clojure-game-geek "0.1.0-SNAPSHOT"
  :description "A tiny BoardGameGeek clone written in Clojure with Lacinia"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [com.walmartlabs/lacinia-pedestal "1.0"]
                 [com.stuartsierra/component "1.0.0"]
                 [io.aviso/logging "1.0"]]
  :profiles {:dev {:resource-paths ["dev-resources"]}}
  :repl-options {:init-ns clojure-game-geek.core})
