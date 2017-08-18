(ns re-frame-tictactoe.events
  (:require [re-frame.core :as re-frame]
            [re-frame-tictactoe.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))
