(ns ttagenda.core
  (:require [ttagenda.db :as db]
            [clojure.string :as str]
            [ttagenda.utils :refer :all]))

(defn- aprint
  "Checks if agenda is empty, if not, call function f with arg r"
  [r f]
  (if (no-agenda? r)
    "Your agenda is empty"
    (f r)))

(defn- prnt-all [r]
  (reduce #(str (:topic %2) "-- " (:id %2) ". " (:content %2) "\n" %1) "" (reverse (sort-by :topic r))))

(defn- prnt [r]
  (reduce #(str (:id %2) ". " (:content %2) "\n" %1) "" (reverse (sort-by :displayid r))))

(defn- print-agenda [r]
  (aprint r prnt))

(defn- print-agenda-all [r]
  (aprint r prnt-all))

(defn- add-agenda! [& {:keys [channel topic username content] :as params}]
  (db/create-agenda! params)
  (str "'" content "'" " added to topic #" topic))

(defn- clear-agenda-topic! [ & {:keys [topic]}]
  (db/clear-agenda-by-topic! {:topic topic})
  (str "#" topic " cleared!"))

(defn- list-agendas-in-topic [& {:keys [channel topic] :as params}]
  (let [agendas (db/find-all-agendas-in-topic params)]
    (print-agenda agendas)))

(defn- list-agendas [channel-id]
  (let [agendas (db/find-all-agendas-in-channel {:channel channel-id})]
    (print-agenda-all agendas)))

(defn- delete-agenda! [& {:keys [channel topic displayid] :as params}]
  (try
    (db/delete-agenda! params)
    (catch Exception e (.getNextException e)))
  "delete successful")

(defn process-request-by-topic [topic channel_id user_name topic-request]
  (let [splits (str/split topic-request #" " 2)
        command (first splits)
        text (second splits)]
    (condp = command
      "add" (add-agenda! :topic topic :channel channel_id :username user_name :content text) 
      "delete" (cond
                 (nil? text) "your input cannot be empty"
                 ;; TODO: check if integer
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
      (process-request-by-topic topic channel_id user_name topic-request))))
