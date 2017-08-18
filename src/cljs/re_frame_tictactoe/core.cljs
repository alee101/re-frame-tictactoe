(ns re-frame-tictactoe.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [re-frame-tictactoe.events]
            [re-frame-tictactoe.subs]
            [re-frame-tictactoe.views :as views]
            [re-frame-tictactoe.config :as config]
            [clojure.string :as string]))

(def board-size 3)

(defn new-game []
  {:board (vec (repeat board-size (vec (repeat board-size nil))))
   :computer-opponent nil
   :player "O"})

(defn winner [board]
  (let [get-winner (fn [s] (if (apply = s) (first s) nil))
        row-winner (map get-winner board)
        col-winner (map get-winner (apply map vector board))
        lft-diag-winner (get-winner (map get board (range 3)))
        rgt-diag-winner (get-winner (map get board (range 2 -1 -1)))]
    (some #{"O" "X"} (or (concat row-winner col-winner [lft-diag-winner rgt-diag-winner])))))

(defn open-cells [board]
  (for [i (range board-size)
        j (range board-size)
        :when (nil? (get-in board [i j]))]
    [i j]))

(defn game-over? [board]
  (or (winner board) (empty? (open-cells board))))

(defn winning-move [board cur-player]
  (first
   (for [i (range board-size)
         j (range board-size)
         :when (and
                (nil? (get-in board [i j]))
                (winner (assoc-in board [i j] cur-player)))]
     [i j])))

(defn other-player [cur-player]
  (if (= cur-player "O")
    "X"
    "O"))

(defn best-move [board cur-player]
  ;; 1. Winning move for cur-player
  ;; 2. Block winning move for opponent
  ;; 3. Random move
  (or
   (winning-move board cur-player)
   (winning-move board (other-player cur-player))
   (rand-nth (open-cells board))))

(defonce app-state
  (reagent/atom (new-game)))

(defn toggle-computer-opponent []
  (let [computer-opponent (:computer-opponent @app-state)
        new-computer-opponent-state (if (nil? computer-opponent)
                                      (other-player (:player @app-state))
                                      nil)]
    (swap! app-state assoc :computer-opponent new-computer-opponent-state)))

(defn make-move [i j]
  (when (not (game-over? (:board @app-state)))
    (swap! app-state assoc
           :board (assoc-in (:board @app-state) [i j] (:player @app-state))
           :player (other-player (:player @app-state)))
    (when (and
           (not (game-over? (:board @app-state)))
           (= (:player @app-state) (:computer-opponent @app-state)))
      (apply make-move (best-move (:board @app-state) (:player @app-state))))))

(defn circle [i j]
  [:circle {:r 0.45
            :stroke "green"
            :stroke-width 0.05
            :fill "none"
            :cx (+ i 0.5)
            :cy (+ j 0.5)}])

(defn cross [i j]
  [:g {:stroke "red"
       :stroke-width 0.05
       :transform (str "translate(" (+ 0.5 i) "," (+ 0.5 j) ")")}
   [:line {:x1 -0.45 :y1 -0.45 :x2 0.45 :y2 0.45}]
   [:line {:x1 0.45 :y1 -0.45 :x2 -0.45 :y2 0.45}]])

(defn cell [i j cell-state]
  [:g
   [:rect
    {:width 1.0
     :height 1.0
     :stroke "black"
     :stroke-width 0.05
     :fill "white"
     :x i
     :y j
     :on-click #(when (nil? cell-state) (make-move i j))}]
   (cond
     (= cell-state "O") [circle i j]
     (= cell-state "X") [cross i j]
     :else nil)])

(defn board [board-state]
  (into [:svg {:view-box (string/join " " [0 0 board-size board-size])
               :width 500
               :height 500}]
        (for [i (range board-size)
              j (range board-size)]
          [cell i j (get-in board-state [i j])])))

(defn tic-tac-toe []
  (let [board-state (:board @app-state)
        game-winner (winner board-state)]
    [:div
     {:style
      {:margin "0 auto"
       :width 500
       :text-align "center"}}
     [:h1
      (cond
        game-winner (str "Game over: " game-winner " has won")
        (empty? (open-cells board-state)) "Game over: Tie"
        :else "Tic-Tac-Toe")]
     [:button
      {:style {:margin 10}
       :on-click toggle-computer-opponent}
      (str "Computer Player: " (if (:computer-opponent @app-state) "ON" "OFF"))]
     [board board-state]
     (when (game-over? board-state)
       [:button
        {:on-click #(reset! app-state (new-game))}
        "Reset"])]))

(defn on-js-reload []
  (reset! app-state (new-game)))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  ;; (reagent/render [views/main-panel]
  ;;                 (.getElementById js/document "app"))
  (reagent/render-component [tic-tac-toe]
                            (. js/document (getElementById "app"))))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
