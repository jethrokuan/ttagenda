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
  (reduce #(str (:id %2) ". " (:content %2) "\n" %1) "" (reverse (sort-by :displayid r))))

(defn print-agenda [r]
  (aprint r prnt))
