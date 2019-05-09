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
                     :toneMappingExposure (Math/pow 0.4 1.0))
           (-> (j/get :domElement) (->> (.appendChild (.-root js/window))))))

(defn light [scene x y z]
  (let [l (three/DirectionalLight. 0xffffff 0.7)]
    (j/update! l :position j/call :set x y z)
    (.add scene l)))

(defonce ^:export scene
         (doto (three/Scene.)
           (light 1 1 1)
           (light 0 0 1)))

(def ^:export camera
  (doto (three/PerspectiveCamera. 60 (/ (.-innerWidth js/window) (.-innerHeight js/window)) 0.1 2000)
    (j/update! :position j/call :set 0 0 700)
    (.lookAt (three/Vector3.))))

;; effects composer for after effects
(def ^:export composer
  (let [w (j/get js/window :innerWidth)
        h (j/get js/window :innerHeight)]
    (doto (three/EffectComposer. renderer)
      (.addPass (j/assoc! (three/RenderPass. scene camera)
                          :renderToScreen true)))))

(defn resize-renderer! []
  (let [w (j/get js/window :innerWidth)
        h (j/get js/window :innerHeight)]
    (j/assoc! camera :aspect (/ w h))
    (.updateProjectionMatrix camera)
    (.setSize renderer w h)))


(defn on-resize [_]
  (resize-renderer!))
