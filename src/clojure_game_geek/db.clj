(ns clojure-game-geek.db
  (:require [clojure.java.jdbc :as jdbc]
            [com.stuartsierra.component :as component])
  (:import (com.mchange.v2.c3p0 ComboPooledDataSource)))

(defn ^:private pooled-data-source
  [host dbname user password port]
  {:datasource (doto (ComboPooledDataSource.)
                 (.setDriverClass "org.postgresql.Driver")
                 (.setJdbcUrl (str "jdbc:postgresql://" host ":" port "/" dbname))
                 (.setUser user)
                 (.setPassword password))})

(defrecord ClojureGameGeekDb
  [ds]

  component/Lifecycle
  (start [this]
    (assoc this :ds (pooled-data-source "localhost" "cggdb" "cgg_role" "lacinia" 5432)))

  (stop [this]
    (-> ds :datasource .close)
    (assoc this :ds nil)))

(defn new-db
  []
  {:db (map->ClojureGameGeekDb {})})

(defn find-game-by-id
  [component game-id]
  (first
    (jdbc/query (:ds component)
                ["select game_id, name, summary, min_players, max_players, created_at, updated_at
                from board_Game where game_id = ?" game-id])))

(defn find-member-by-id
  [db member-id]
  (->> db
       :data
       deref
       :members
       (filter #(= member-id (:id %)))
       first))

(defn list-designers-for-game
  [db game-id]
  (let [designers (:designers (find-game-by-id db game-id))]
    (->> db
         :data
         deref
         :designers
         (filter #(contains? designers (:id %))))))

(defn list-games-for-designer
  [db designer-id]
  (->> db
       :data
       deref
       :games
       (filter #(-> % :designers (contains? designer-id)))))

(defn list-ratings-for-game
  [db game-id]
  (->> db
       :data
       deref
       :ratings
       (filter #(= game-id (:game_id %)))))

(defn list-ratings-for-member
  [db member-id]
  (->> db
       :data
       deref
       :ratings
       (filter #(= member-id (:member_id %)))))

(defn ^:private apply-game-rating
  [game-ratings game-id member-id rating]
  (->> game-ratings
       (remove #(and (= game-id (:game_id %))
                     (= member-id (:member_id %))))
       (cons {:game_id   game-id
              :member_id member-id
              :rating    rating})))

(defn upsert-game-rating
  "Add a new game rating, or changes the value of an existing game rating."
  [db game-id member-id rating]
  (-> db
      :data
      (swap! update :ratings apply-game-rating game-id member-id rating)))
