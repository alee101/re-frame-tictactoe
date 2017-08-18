(ns re-frame-tictactoe.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :board
 (fn [db]
   (:board db)))

(re-frame/reg-sub
 :computer-opponent
 (fn [db]
   (:computer-opponent db)))
