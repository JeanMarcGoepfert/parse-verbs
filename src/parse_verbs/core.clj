(ns parse-verbs.core
  (:require [clojure.data.json :as json]
            [clojure.set :refer [rename-keys]]
            [clojure.string :refer [lower-case]]
            ))

(def verbs
  (json/read-str (slurp "resources/verbs.txt")))

(defn strip-stuff [verbs]
  (let [wanted-keys ["infinitive"
                     "infinitive_english"
                     "mood"
                     "mood_english"
                     "tense"
                     "tense_english"
                     "form_1s"
                     "form_2s"
                     "form_3s"
                     "form_1p"
                     "form_2p"
                     "form_3p"
                     ]]
    (map #(select-keys % wanted-keys) verbs)))

(defn rename [verbs]
  (map #(rename-keys %
               {"form_1s" "Yo"
                "form_2s" "Tú"
                "form_3s" "El/Ella/Usted"
                "form_1p" "Nosotr(o/a)s"
                "form_2p" "Vosotr(o/a)s"
                "form_3p" "Ell(o/a)s/Ustedes"}) verbs))

(def expected
  {"hablar" {:translation "to eat"
             :useage {"" {:present {"yo" "hablo"
                                             "tu" "hablas"}}
                     :subjunctive {:present {"yo" "hable"
                                             "tu" "hables"}}}}})

(defn create-thing [verbs]
  (reduce
    (fn [acc v]
      (let [tense (lower-case (v "tense_english"))
            mood (lower-case (v "mood_english"))
            rs (select-keys v ["Yo"
                               "Tú"
                               "El/Ella/Usted"
                               "Nosotr(o/a)s"
                               "Vosotr(o/a)s"
                               "Ell(o/a)s/Ustedes"])]
        (assoc-in acc [(v "infinitive") :useage mood tense] rs)))
    {}
    verbs))

(def a (strip-stuff verbs))
(def b (rename a))
(def c (create-thing b))

(spit "resources/verbs2.json" (json/write-str c))
