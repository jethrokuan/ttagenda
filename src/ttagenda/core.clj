(ns ttagenda.core
  (:require [ttagenda.db :as db]
            [clojure.string :as str]
            [ttagenda.printer :as p]
            [ttagenda.webhook :refer [post-to-agenda]]
            [ttagenda.keytable :refer [make-keytable]]))

(defn- add-agenda! [& {:keys [channel topic user content] :as params}]
  (if (nil? content)
    "your input cannot be empty"
    (try
      (db/create-agenda! (assoc params :username user))
      (post-to-agenda {:text (str "@" user " added " "_'" content "'_" " to #" topic)
                       :channel channel})
      (catch Exception e (str "caught exception: " (.getNextException e))))))

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

(defn- delete-agenda! [& {:keys [topic id user] :as params}]
  (if (nil? id)
    "your input cannot be empty"
    (let [iid (read-string id)]
      (cond
        (not (integer? iid)) "your input must be an integer"
        :else (try
                (if-let [item (seq (db/find-agenda {:id iid}))]
                  (if (= 0 (db/delete-agenda! (assoc params :id iid)))              
                    "The id you chose is not deletable with your current command"
                    (post-to-agenda {:text (str "@" user " deleted _\""(-> item first :content) "\"_" " from #" topic)
                                     :channel (:channel params)}))
                  "there is no item with this id")                
                (catch Exception e (str "caught exception: " (.getNextException e))))))))

(defn- clear-agenda! [& {:keys [channel item] :as params}]
  (prn params)
  (let [keynum (make-keytable)]
    (if (= 1 (db/insert-key-table! {:keynum keynum :channel channel :item item}))
      (str "to confirm your request, type /agenda keytable " keynum)
      "operation failed")))

(defn- clear-agenda-for-real! [& {:keys [keynum channel user] :as params}]
  (let [r (db/find-key {:keynum keynum})]
    (if (seq r)
      (let [item (:item (first r))]
        (condp = item
          "clear" (try
                    (db/clear-agenda-by-channel! {:channel channel})                    
                    (try
                      (db/remove-from-keytable! {:keynum keynum})
                      (catch Exception e (str "caught exception: " (.getNextException e))))
                    (post-to-agenda {:text (str "@" user " cleared the channel!")
                                     :channel channel})
                    (catch Exception e (str "caught exception: " (.getNextException e))))
          (try
            (db/clear-agenda-by-topic! {:topic item :channel channel})
            (try
              (db/remove-from-keytable! {:keynum keynum})
              (catch Exception e (str "caught exception: " (.getNextException e))))
            (post-to-agenda {:text (str "@" user " cleared topic #" item)
                             :channel channel})
            (catch Exception e (str "caught exception: " (.getNextException e))))
          ))
      "No matching keytable found. Please try again."
      #_(try 
          (db/clear-agenda-by-channel! params)
          (str "Channel #" channel_name " cleared!")
          (catch Exception e (str "caught exception: " (.getNextException e)))))))

(defn process-request-by-topic [{:keys [user_name topic channel_id topic-request] :as params}]
  (let [splits (str/split topic-request #" " 2)
        command (first splits)
        text (second splits)]
    (condp = command
      "add" (add-agenda! :topic topic :channel channel_id :user user_name :content text) 
      "delete" (delete-agenda! :topic topic :channel channel_id :id text :user user_name)
      "clear" (clear-agenda! :channel channel_id :item topic)
      "list" (list-agendas-in-topic :topic topic :channel channel_id)
      "not a valid command")))

(defn- display-documentation []
  "\n*AGENDA DOCUMENTATION*\n
   ----------------------------------------------------------------\n
   */agenda list* : Lists agendas in default topic, shows other available topics in channel\n
   */agenda add [text]* : Adds to default channel topic\n
   */agenda delete [id no.]* : Delete id number in default topic\n
   \t\t\t \"deletion successful\" is returned when something is actually deleted\n
   \t\t\t \"nothing deleted ...\" is returned when ... nothing is deleted\n
   \t\t\t*you can only delete items within your channel*\n
   */agenda clear* : clears all agendas in channel.\n
   \t\t\tTriggers the creation of a keytable, because this is a dangerous command.\n
  */agenda [topic] [add | list | delete | clear ]* : self-explanatory actions on sub-topics within channel\n
  * /agenda keytable [keynum]* : enter the passphrase here te confirm the clear command!"
  )

(defn process-request [{:keys [channel_id user_name text channel_name] :as params}]
  (let [splits (str/split text #" " 2)
        topic (first splits)
        topic-request (last splits)]
    (condp = topic
      "" (list-agendas-in-channel :channel channel_id :topic channel_name)
      "list" (list-agendas-in-channel :channel channel_id :topic channel_name)
      "add"  (process-request-by-topic (assoc params :topic-request text :topic channel_name))
      "delete" (process-request-by-topic (assoc params :topic-request text :topic channel_name))
      "clear" (clear-agenda! :channel channel_id :item text)
      "help" (display-documentation)
      "keytable" (clear-agenda-for-real! :keynum topic-request :channel channel_id :user user_name)
      (process-request-by-topic (assoc params :topic-request topic-request :topic topic :user user_name)))))
