(ns ttagenda.printer
  (:require [ttagenda.utils :refer :all]))

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

(defn print-agenda [r]
  (aprint r prnt))

(defn print-agenda-all [r]
  (aprint r prnt-all))
