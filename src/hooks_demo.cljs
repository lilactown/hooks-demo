(ns hooks-demo
  (:require ["react-dom" :as react-dom]
            ["react" :as react]
            [hooks-demo.hooks :as hooks :refer [<-state <-context]]
            [hx.react :as hx :include-macros true]))

(defonce state-context (react/createContext))

(def Provider (.-Provider state-context))

(hx/defnc Display [_]
  (let [{:keys [name count]} @(<-context state-context)]
    (hx/c [:div name ": " count])))

(hx/defnc Input [_]
  (let [!state (<-context state-context)]
    (hx/c [:div
           [:input {:type "text" :value (:name @!state)
                    :on-change
                    #(swap! !state assoc :name (.. % -target -value))}]])))

(hx/defnc CountButtons [_]
  (let [!state (<-context state-context)]
    (hx/c [:div
           [:button {:on-click #(swap! !state update :count inc)} "inc"]
           [:button {:on-click #(swap! !state update :count dec)} "dec"]])))

(hx/defnc App [_]
  (let [!state (<-state {:name "Will" :count 1})]
    (hx/c [Provider {:value !state}
           [:div
            [Display]
            [Input]
            [CountButtons]]])))

(react-dom/render (hx/c [App]) (js/document.getElementById "app"))
