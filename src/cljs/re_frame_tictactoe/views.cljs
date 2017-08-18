(ns re-frame-tictactoe.views
  (:require [re-frame.core :as re-frame]
            [re-frame-tictactoe.game :as game]
            [clojure.string :as string]))

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
     :on-click #(when (nil? cell-state) (re-frame/dispatch [:click-cell [i j]]))}]
   (cond
     (= cell-state "O") [circle i j]
     (= cell-state "X") [cross i j]
     :else nil)])

(defn board [board-state]
  (into [:svg {:view-box (string/join " " [0 0 game/board-size game/board-size])
               :width 500
               :height 500}]
        (for [i (range game/board-size)
              j (range game/board-size)]
          [cell i j (get-in board-state [i j])])))

(defn tic-tac-toe []
  (let [board-state @(re-frame/subscribe [:board])
        game-winner (game/winner board-state)]
    [:div
     {:style
      {:margin "0 auto"
       :width 500
       :text-align "center"}}
     [:h1
      (cond
        game-winner (str "Game over: " game-winner " has won")
        (empty? (game/open-cells board-state)) "Game over: Tie"
        :else "Tic-Tac-Toe")]
     [:button
      {:style {:margin 10}
       :on-click #(re-frame/dispatch [:toggle-computer-opponent])}
      (str "Computer Player: " (if @(re-frame/subscribe [:computer-opponent]) "ON" "OFF"))]
     [board board-state]
     (when (game/game-over? board-state)
       [:button
        {:on-click #(re-frame/dispatch [:reset-game])}
        "Reset"])]))
