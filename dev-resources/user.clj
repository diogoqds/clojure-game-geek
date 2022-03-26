(ns user
  (:require [clojure-game-geek.schema :as s]
            [com.walmartlabs.lacinia :as lacinia]
            [com.walmartlabs.lacinia.pedestal :as lp]
            [clojure-game-geek.system :as system]
            [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]
            [clojure.java.browse :refer [browse-url]]
            [clojure.walk :as walk])
  (:import (clojure.lang IPersistentMap)))

(defonce system (system/new-system))

(defn simplify
  [m]
  (walk/postwalk
    (fn [node]
      (cond
        (instance? IPersistentMap node)
        (into {} node)

        (seq? node)
        (vec node)

        :else
        node))
    m))

(defn q
  [query-string]
  (-> system
      :schema-provider
      :schema
      (lacinia/execute query-string nil nil)
      simplify))

(defonce server nil)

(defn start
  []
  (alter-var-root #'system component/start-system)
  (browse-url "http://localhost:8888/")
  :started)

(defn stop
  []
  (alter-var-root #'system component/stop-system)
  :stopped)

(start)
(comment
  (start))