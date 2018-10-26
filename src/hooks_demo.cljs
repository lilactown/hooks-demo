(ns hooks-demo
  (:require ["react" :as react]
            ["react-dom" :as react-dom]
            [sablono.core :refer-macros [html]]))

(set! js/React react)

;;
;; Example using new useState hook
;;

(defn UseState []
  (let [[count set-count] (react/useState 0)]
    (html [:div
           [:strong "UseState"]
           " "
           [:button {:on-click #(set-count (inc count))}
            count]])))

;;
;; Example using custom use-atom hook
;;

(defn use-atom
  ([a] (use-atom a []))
  ([a deps]
   (let [[v u] (react/useState @a)]
     ;; useEffect creates the subscription to the atom
     (react/useEffect
      (fn []
        (println "adding watch")
        (add-watch a :use-atom
                   ;; update the react state on each change
                   (fn [_ _ _ v'] (u v')))
        ;; return a function to tell react how to unsubscribe
        #(do
           (println "removing watch")
           (remove-watch a :use-atom)))
      ;; pass in deps vector as an array
      (clj->js deps))
     ;; return value of useState on each run
     v)))

(defonce my-state (atom 0))

(defn UseAtom []
  (let [count (use-atom my-state)]
    (html [:div
           [:strong "UseAtom"]
           [:button {:on-click #(swap! my-state inc)}
            count]])))

(defn App []
  (html [:div
         (UseState)
         (UseAtom)]))

(react-dom/render (react/createElement App) (js/document.getElementById "app"))
