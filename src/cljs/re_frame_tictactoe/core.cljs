(ns re-frame-tictactoe.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [re-frame-tictactoe.events]
            [re-frame-tictactoe.subs]
            [re-frame-tictactoe.views :as views]
            [re-frame-tictactoe.config :as config]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/tic-tac-toe]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))

(defn on-js-reload []
  (init))
