(ns hooks-demo.hooks
  (:require ["react" :as react]))

(def ->state react/useState)

(def ->effect react/useEffect)

;;
;; Example of a custom with-atom hook
;;

(defn ->deref
  ;; if no deps are passed in, we assume we only want to run
  ;; subscrib/unsubscribe on mount/unmount
  ([a] (->deref a []))
  ([a deps]
   ;; create a react/useState hook to track and trigger renders
   (let [[v u] (->state @a)]
     ;; react/useEffect hook to create and track the subscription to the iref
     (->effect
      (fn []
        (println "adding watch")
        (add-watch a :use-atom
                   ;; update the react state on each change
                   (fn [_ _ _ v'] (u v')))
        ;; return a function to tell react hook how to unsubscribe
        #(do
           (println "removing watch")
           (remove-watch a :use-atom)))
      ;; pass in deps vector as an array
      (clj->js deps))
     ;; return value of useState on each run
     v)))

;; credit Roman Liutikov (@roman01la)
;; https://twitter.com/roman01la/status/1055905216166543361

(deftype State [react-ref]
  IDeref
  (-deref [_]
    (first react-ref))

  IReset
  (-reset! [_ v']
    ((second react-ref) v')
    v')

  ISwap
  (-swap! [o f]
    (-reset! o (f (-deref o)))))

(defn ->atom [initial]
  (State. (->state initial)))
