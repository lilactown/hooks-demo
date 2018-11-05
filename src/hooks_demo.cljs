(ns hooks-demo
  (:require ["react" :as react]
            ["react-dom" :as react-dom]
            [re-frame.core]
            [hooks-demo.hooks :refer [<-state <-!state <-deref <-sub <-effect]]
            [hx.react :as hx :include-macros true]))

;;
;; Example using new useState hook
;;

(hx/defnc UseStateHook [_]
  (let [[count set-count] (<-state 0)]
    [:div
     [:strong "<-state Hook:"]
     " "
     [:button {:on-click #(set-count (inc count))}
      count]]))

;;
;; Example of a custom hook to watch atoms
;;

(defonce my-state (atom 0))

(hx/defnc DerefHook [_]
  (let [count (<-deref my-state)]
    [:div
     [:strong "<-deref Hook:"]
     " "
     [:button {:on-click #(swap! my-state inc)}
      count]]))

;;
;; Example of representing React state using atom protocols
;;

(hx/defnc StateAtomHook [_]
  (let [count (<-!state 0)]
    [:div
     [:strong "<-!state Hook:"]
     " "
     [:button {:on-click #(swap! count inc)}
      @count]]))

;;
;; Example of using re-frame subscriptions
;;

(re-frame.core/reg-event-db
  :inc
  (fn [db _]
    (update db :counter (fnil inc 0))))

(re-frame.core/reg-sub
  :counter
  (fn [db _]
    (:counter db 0)))

(hx/defnc ReframeHook [_]
  (let [count (<-sub [:counter])]
    [:div
     [:strong "re-frame <-sub Hook:"]
     " "
     [:button {:on-click #(re-frame.core/dispatch [:inc])}
      count]]))

(hx/defnc App [_]
  [:div
   [UseStateHook]
   [DerefHook]
   [StateAtomHook]
   [ReframeHook]])

(react-dom/render (hx/f [App]) (js/document.getElementById "app"))
