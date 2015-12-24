(ns ttagenda.keytable
  (:require [clojure.string :as str]))

(def words ["nsb" "moon" "zorn" "runed" "of" "berne" "spoon" "thief" "cia" "hope" "mph" "gems" "yazd" "pan" "tuvalu" "ucla" "vlad" "cat" "starve" "earp" "hhd" "unit" "alum" "jake" "ice" "thong" "week" "corn" "brunch" "skint" "chirp" "dow" "wive" "jeh" "pounce" "chet" "pest" "tam" "spike" "kink" "sken" "wart" "aim" "bine" "rennes" "dote" "comdr" "skiff" "peal" "soil" "brine" "leal" "dom" "wundt" "bai" "steam" "slough" "neb" "boots" "cow" "coign" "seen" "tinge" "kirsch" "yap" "perm" "ley" "hong" "west" "phies" "comm" "recit" "steppes" "fort" "and" "hell" "omsk" "sone" "blitz" "tale" "caste" "nook" "ect" "nam" "don" "low" "scowl" "shru" "marsh" "frame" "force" "lobe" "mauve" "blintze" "wank" "scull" "grave" "vined" "wyte" "toom"])

(defn make-keytable
  "Generate and concat 3 random words"
  []
  (str/join (repeatedly 3 (partial rand-nth words))))
