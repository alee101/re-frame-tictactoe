(ns re-frame-tictactoe.game)

(def board-size 3)

(defn new-game []
  {:board (vec (repeat board-size (vec (repeat board-size nil))))
   :computer-opponent nil
   :player "O"})

(defn other-player [cur-player]
  (if (= cur-player "O")
    "X"
    "O"))

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

(defn best-move [board cur-player]
  ;; 1. Winning move for cur-player
  ;; 2. Block winning move for opponent
  ;; 3. Random move
  (or
   (winning-move board cur-player)
   (winning-move board (other-player cur-player))
   (rand-nth (open-cells board))))

(defn make-move [cur-state [i j]]
  (when (not (game-over? (:board cur-state)))
    (let [new-board (assoc-in (:board cur-state) [i j] (:player cur-state))
          new-player (other-player (:player cur-state))
          new-state (assoc cur-state :board new-board :player new-player)]
      (if (and
           (not (game-over? new-board))
           (= new-player (:computer-opponent cur-state)))
        (make-move new-state (best-move new-board new-player))
        new-state))))
