(ns pipes.core
  (:require [applied-science.js-interop :as j]
            [pipes.stage :as stage]
            ["three-full" :as three]
            ["easing" :as easing]
            ["bezier-easing" :as BezierEasing]))

(defonce last-tick (atom 0))
(defonce current-frame (atom 0))
(defonce mesh (atom nil))
(defonce meshes (atom []))


(defn r [v]
  (* (js/Math.random) v))

(defn setup-cubes []
  (let [geom (three/BoxBufferGeometry. 25 25 25)
        mx -600
        my 600
        step 100]
    (doseq [x (range mx my step)
            y (range mx my step)
            z (range mx my step)]
      (let [mesh (three/Mesh. geom (three/MeshLambertMaterial. #js {:color (* (js/Math.random) 0xffffff)}))]
        (j/assoc-in! mesh [:position :x] x)
        (j/assoc-in! mesh [:position :y] y)
        (j/assoc-in! mesh [:position :z] z)
        (j/assoc-in! mesh [:rotation :x] (r (* 2 Math/PI)))
        (j/assoc-in! mesh [:rotation :y] (r (* 2 Math/PI)))
        (j/assoc-in! mesh [:rotation :z] (r (* 2 Math/PI)))
        (swap! meshes conj mesh)
        (.add stage/scene mesh)))))

(defn render []

  (let [mesheses @meshes
        has-mesh? (seq mesheses)
        fps 0.5
        num-frames 100
        frame-duration (/ 1000 fps)
        time (.now js/Date)
        tick @last-tick
        frame @current-frame]

    (when (>= time (+ @last-tick frame-duration))
      (when (not has-mesh?)
        (doseq [m mesheses]
          (.remove stage/scene m))
        (setup-cubes))
      (prn ::render tick frame)
      (reset! last-tick time)
      (swap! current-frame #(mod (inc %) num-frames))))
  (when stage/composer
    (.render stage/composer)))                              ;; render from the effects composer

(defn animate []
  (let [f (/ (js/Date.now) 8000)]
    (j/assoc-in! stage/scene [:rotation :y] f))
  (.requestAnimationFrame js/window animate)
  (render))

(defn init []

  (.addEventListener js/window "resize" stage/on-resize)
  (animate))

