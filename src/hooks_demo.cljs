(ns hooks-demo
  (:require ["react-dom" :as react-dom]
            ["react" :as react]
            [hooks-demo.hooks :as hooks :refer [<-reducer <-context]]
            [hx.react :as hx :include-macros true]))

(defonce state-context (react/createContext))

(def Provider (.-Provider state-context))

(defn reducer [state [type value]]
  (case type
    :demo/update-name (assoc state :name value)
    :demo/inc-count (update state :count + value)
    :demo/dec-count (update state :count - value)))

(hx/defnc Display [_]
  (let [[{:keys [name count]} _] (<-context state-context)]
    (hx/c [:div name ": " count])))

(hx/defnc Input [_]
  (let [[{:keys [name]} dispatch!] (<-context state-context)]
    (hx/c [:div
           [:input {:type "text" :value name
                    :on-change
                    #(dispatch! [:demo/update-name (.. % -target -value)])}]])))

(hx/defnc CountButtons [_]
  (let [[_ dispatch!] (<-context state-context)]
    (hx/c [:div
           [:button {:on-click #(dispatch! [:demo/inc-count 1])} "inc"]
           [:button {:on-click #(dispatch! [:demo/dec-count 1])} "dec"]])))

(hx/defnc App [_]
  (let [state (<-reducer reducer {:name "Will" :count 1})]
    (hx/c [Provider {:value state}
           [:div
            [Display]
            [Input]
            [CountButtons]]])))

(react-dom/render (hx/c [App]) (js/document.getElementById "app"))
