(ns ttagenda.core
  (:require [ttagenda.db :as db]
            [clojure.string :as str]
            [ttagenda.printer :as p]))

(defn- add-agenda! [& {:keys [channel topic username content] :as params}]
  (db/create-agenda! params)
  (str "'" content "'" " added to topic #" topic))

(defn- clear-agenda-topic! [ & {:keys [topic]}]
  (db/clear-agenda-by-topic! {:topic topic})
  (str "#" topic " cleared!"))

(defn- list-agendas-in-topic [& {:keys [channel topic] :as params}]
  (let [agendas (db/find-all-agendas-in-topic params)]
    (p/print-agenda agendas)))

(defn- list-agendas [channel-id]
  (let [agendas (db/find-all-agendas-in-channel {:channel channel-id})]
    (p/print-agenda-all agendas)))

(defn- delete-agenda! [params]
  (try
    (db/delete-agenda! params)
    (catch Exception e (.getNextException e)))
  "delete successful")

(defn process-request-by-topic [{:keys [user_name topic channel_id topic-request] :as params}]
  (let [splits (str/split topic-request #" " 2)
        command (first splits)
        text (second splits)]
    (condp = command
      "add" (add-agenda! :topic topic :channel channel_id :username user_name :content text) 
      "delete" (cond
                 (nil? text) "your input cannot be empty"
                 (integer? (read-string text)) "your input must be an integer"
                 :else (delete-agenda! :topic topic :id text :channel channel_id))
      "clear" (clear-agenda-topic! :topic topic)
      "list" (list-agendas-in-topic :topic topic :channel channel_id)
      "not a valid command")))

(defn process-request [{:keys [channel_id user_name text] :as params}]
  (prn db/db-spec)
  (let [splits (str/split text #" " 2)
        topic (first splits)
        topic-request (last splits)]
    (condp = topic
      "list" (list-agendas channel_id)
      "add" "Invalid topic name 'add'"
      "delete" "Invalid topic name 'delete'"
      "clear" "Invalid topic name 'clear'"
      (process-request-by-topic (assoc params :topic-request topic-request)))))
