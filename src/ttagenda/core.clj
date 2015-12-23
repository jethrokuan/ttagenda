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

(defn- list-agendas-in-channel [& {:keys [channel topic] :as params}]
  (let [agendas (db/find-all-agendas-in-topic params)
        topics (db/find-all-topics-in-channel params)]
    (str (p/print-agenda agendas) "\n"
         "All topics in channel: " (p/print-topics topics))))

(defn- delete-agenda! [& {:keys [id] :as params}]
  (let [iid (read-string id)]
    (cond
      (nil? iid) "your input cannot be empty"
      (not (integer? iid)) "your input must be an integer"
      :else (try
              (db/delete-agenda! (assoc params :id iid))
              "deletion successful!"
              (catch Exception e (str "caught exception: " (.getNextException e)))))))

(defn process-request-by-topic [{:keys [user_name topic channel_id topic-request] :as params}]
  (let [splits (str/split topic-request #" " 2)
        command (first splits)
        text (second splits)]
    (condp = command
      "add" (add-agenda! :topic topic :channel channel_id :username user_name :content text) 
      "delete" (delete-agenda! :topic topic :channel channel_id :id text)
      "clear" (clear-agenda-topic! :topic topic)
      "list" (list-agendas-in-topic :topic topic :channel channel_id)
      "not a valid command")))

(defn process-request [{:keys [channel_id user_name text channel_name] :as params}]
  (let [splits (str/split text #" " 2)
        topic (first splits)
        topic-request (last splits)]
    (condp = topic
      "list" (list-agendas-in-channel :channel channel_id :topic channel_name)
      "add"  (process-request-by-topic (assoc params :topic-request text :topic channel_name))
      "delete" "Invalid topic name 'delete'"
      "clear" "Invalid topic name 'clear'"
      (process-request-by-topic (assoc params :topic-request topic-request :topic topic)))))
