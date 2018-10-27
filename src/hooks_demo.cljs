(ns hooks-demo
  (:require ["react-dom" :as react-dom]
            [hooks-demo.hooks :as hooks :refer [->state ->deref ->atom]]
            [hx.react :as hx :include-macros true]))

;;
;; Example using new useState hook
;;

(hx/defnc UseState [_]
  (let [[count set-count] (->state 0)]
    (hx/c [:div
           [:strong "UseState"]
           " "
           [:button {:on-click #(set-count (inc count))}
            count]])))

(defonce my-state (atom 0))

(hx/defnc UseDeref [_]
  (let [count (->deref my-state)]
    (hx/c [:div
           [:strong "UseDeref"]
           " "
           [:button {:on-click #(swap! my-state inc)}
            count]])))

(hx/defnc UseAtom [_]
  (let [count (->atom 0)]
    (hx/c [:div
           [:strong "UseAtom"]
           " "
           [:button {:on-click #(swap! count inc)}
            @count]])))

(hx/defnc App [_]
  (hx/c [:div
         [UseState]
         [UseDeref]
         [UseAtom]]))

(react-dom/render (hx/c [App]) (js/document.getElementById "app"))
