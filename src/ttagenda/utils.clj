(ns ttagenda.utils)

(defn no-agenda? 
  "Returns true if results array is empty"
  [r]
  (= 0 (count r)))
