(ns ttagenda.printer
  (:require [clojure.string :as str]
            [ttagenda.utils :refer :all]))

(defn- aprint
  "Checks if agenda is empty, if not, call function f with arg r"
  [r f]
  (if (no-agenda? r)
    "Your agenda is empty"
    (f r)))

(defn print-topics
  "Prints list of topics from result of find-all-topics-in-channel"
  [r]
  (str/join #", " (map :topic r)))

(defn- prnt [r]
  (let [topic (first r)
        merged (map vector (iterate inc 1) r)]
    (str "*Topic: #" (:topic (first r)) "*\n"
         "----------------------------------------------------------------------------------------------------\n"
         "  id            user                              item                                                   \n"
         "----------------------------------------------------------------------------------------------------\n"
         (reduce
          (fn [i v]
            (let [displayid (first v)
                  result (last v)]            
              (str "(" (:id result) ")"
                   "\t\t" (:username result)
                   (if (> 10 (:id result)) "\t\t\t\t\t\t\t" "\t\t\t\t\t\t ")
                   displayid
                   (if (> 10  displayid) ".\t\t" ".\t")
                   (:content result)
                   "\n"
                   i)))
          " "
          (reverse merged)))))

(defn print-agenda [r]
  (aprint r prnt))
