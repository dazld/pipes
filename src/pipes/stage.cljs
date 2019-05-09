(ns pipes.stage
  (:require [applied-science.js-interop :as j]
            ["three-full" :as three]
            ["easing" :as easing]
            ["bezier-easing" :as BezierEasing]))


(defonce ^:export renderer
         (doto (three/WebGLRenderer. (clj->js :antialias true))
           (.setPixelRatio (.-devicePixelRatio js/window))
           (.setSize (.-innerWidth js/window) (.-innerHeight js/window))
           (j/assoc! :physicallyCorrectLights true
                     :antialias true
                     :gammaInput true
                     :gammaOutput true
                     :toneMapping three/ReinhardToneMapping
                     :toneMappingExposure (Math/pow 1.4 5.0))
           (-> (j/get :domElement) (->> (.appendChild (.-root js/window))))))

(defonce ^:export scene
         (three/Scene.))

(def ^:export camera
  (doto (three/PerspectiveCamera. 75 (/ (.-innerWidth js/window) (.-innerHeight js/window)) 0.1 1000)
    (j/update! :position j/call :set 0 0 70)
    (.lookAt (three/Vector3.))))

;; effects composer for after effects
(def ^:export composer
  (let [w (j/get js/window :innerWidth)
        h (j/get js/window :innerHeight)]
    (doto (three/EffectComposer. renderer)
      (.addPass (three/RenderPass. scene camera))
      (.addPass (three/UnrealBloomPass. (three/Vector2. w h) ; viewport resolution
                                        0.3   ; strength
                                        0.2   ; radius
                                        0.8)) ; threshold
      (.addPass (j/assoc! (three/FilmPass. 0.25  ; noise intensity
                                           0.26  ; scanline intensity
                                           648   ; scanline count
                                           false); grayscale
                          :renderToScreen true)))))

(defn resize-renderer! []
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)]
    (j/assoc! camera :aspect (/ w h))
    (.updateProjectionMatrix camera)
    (.setSize renderer w h)))


(defn on-resize [_]
  (resize-renderer!))
