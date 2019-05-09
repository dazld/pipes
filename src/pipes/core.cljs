(ns pipes.core
  (:require [applied-science.js-interop :as j]
            [pipes.stage :as stage]
            ["three-full" :as three]
            ["easing" :as easing]
            ["bezier-easing" :as BezierEasing]))

(defonce last-tick (atom 0))
(defonce current-frame (atom 0))
(defonce mesh (atom nil))

(defn render []
  (let [fps 1.0
        num-frames 100
        frame-duration (/ 1000 fps)
        time (.now js/Date)
        tick @last-tick
        frame @current-frame]
    (when (>= time (+ @last-tick frame-duration))
      (prn ::render tick frame)
      (reset! last-tick time)
      (swap! current-frame #(mod (inc %) num-frames))))

  (.render stage/composer nil)) ;; render from the effects composer

(defn animate []
  (.requestAnimationFrame js/window animate)
  (render))

(defn init []
  (animate)
  (.addEventListener js/window "resize" stage/on-resize)
  (prn ::hi))