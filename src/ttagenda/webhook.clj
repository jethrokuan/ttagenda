(ns ttagenda.webhook
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [environ.core :refer [env]]))

(defn- post-to-slack [url payload]
  (client/post url {:body (json/write-str payload)
                    :content-type :json}))

(def post-to-agenda
  (partial post-to-slack (or
                          (System/getProperty "WEBHOOK_URL")
                          (env :hook-url))))
