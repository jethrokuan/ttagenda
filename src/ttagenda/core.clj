(ns ttagenda.core
  (:require [ttagenda.db :as db]
            [clojure.string :as str]
            [ttagenda.printer :as p]
            [ttagenda.webhook :refer [post-to-agenda]]))

(defn- add-agenda! [& {:keys [channel topic username content] :as params}]
  (if (nil? content)
    "your input cannot be empty"
    (try
      (db/create-agenda! params)
      (post-to-agenda {:text (str "_'" content "'_" " added to topic #" topic)
                       :channel channel})
      (catch Exception e (str "caught exception: " (.getNextException e))))))

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
         "All topics in channel: " (if (= 0 (count topics))
                                     "yay! nothing here!"
                                     (p/print-topics topics)))))

(defn- delete-agenda! [& {:keys [id] :as params}]
  (if (nil? id)
    "your input cannot be empty"
    (let [iid (read-string id)]
      (cond
        (not (integer? iid)) "your input must be an integer"
        :else (try
                (if (= 0 (db/delete-agenda! (assoc params :id iid)))                 
                  "nothing deleted... please check your id input again."
                  "deletion successful!")
                (catch Exception e (str "caught exception: " (.getNextException e))))))))

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

(defn- clear-channel! [& {:keys [channel_id channel_name] :as params}]
  (try
    (db/clear-agenda-by-channel! params)
    (str "Channel #" channel_name " cleared!")
    (catch Exception e (str "caught exception: " (.getNextException e)))))

(defn process-request [{:keys [channel_id user_name text channel_name] :as params}]
  (let [splits (str/split text #" " 2)
        topic (first splits)
        topic-request (last splits)]
    (condp = topic
      "list" (list-agendas-in-channel :channel channel_id :topic channel_name)
      "add"  (process-request-by-topic (assoc params :topic-request text :topic channel_name))
      "delete" (process-request-by-topic (assoc params :topic-request text :topic channel_name))
      "clear" (clear-channel! :channel channel_id :channel_name channel_name)
      (process-request-by-topic (assoc params :topic-request topic-request :topic topic)))))
