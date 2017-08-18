(ns re-frame-tictactoe.events
  (:require [re-frame.core :as re-frame]
            [re-frame-tictactoe.game :as game]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   (game/new-game)))

(re-frame/reg-event-db
 :click-cell
 (fn [db [_ [i j]]]
   (game/make-move db [i j])))

(re-frame/reg-event-db
 :reset-game
 (fn [db _]
   (game/new-game)))

(re-frame/reg-event-db
 :toggle-computer-opponent
 (fn [db _]
  (let [new-computer-opponent-state (if (nil? (:computer-opponent db))
                                      (game/other-player (:player db))
                                      nil)]
    (assoc db :computer-opponent new-computer-opponent-state))))
