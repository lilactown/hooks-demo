(ns hooks-demo
  (:require ["react" :as react]
            ["react-dom" :as react-dom]
            [hx.react :as hx :include-macros true]))

;;
;; Example using new useState hook
;;

(hx/defnc UseState [_]
  (let [[count set-count] (react/useState 0)]
    [:div
     [:strong "UseState"]
     " "
     [:button {:on-click #(set-count (inc count))}
      count]]))

;;
;; Example of a custom use-atom hook
;;

(defn use-atom
  ;; if no deps are passed in, we assume we only want to run
  ;; subscrib/unsubscribe on mount/unmount
  ([a] (use-atom a []))
  ([a deps]
   ;; create a react/useState hook to track and trigger renders
   (let [[v u] (react/useState @a)]
     ;; react/useEffect hook to create and track the subscription to the iref
     (react/useEffect
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

(defonce my-state (atom 0))

(hx/defnc UseAtom [_]
  (let [count (use-atom my-state)]
    [:div
     [:strong "UseAtom"]
     " "
     [:button {:on-click #(swap! my-state inc)}
      count]]))

(hx/defnc App [_]
  [:div
   [UseState]
   [UseAtom]])

(react-dom/render (hx/f [App]) (js/document.getElementById "app"))
