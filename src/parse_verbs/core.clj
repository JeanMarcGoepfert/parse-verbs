(ns parse-verbs.core
  (:require [clojure.data.json :as json]
            [clojure.set :refer [rename-keys]]
            [clojure.string :refer [lower-case]]
            [cheshire.core :refer :all]
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
                     "verb_english"
                     "form_1s"
                     "form_2s"
                     "form_3s"
                     "form_1p"
                     "form_2p"
                     "form_3p"
                     "gerund"
                     "gerund_english"
                     "pastparticiple"
                     "pastparticiple_english"]]
    (map #(select-keys % wanted-keys) verbs)))

(defn create-thing [verbs]
  (reduce
    (fn [acc v]
      (let [tense (lower-case (v "tense_english"))
            mood (lower-case (v "mood_english"))
            rs (select-keys v ["form_1s"
                               "form_2s"
                               "form_3s"
                               "form_1p"
                               "form_2p"
                               "form_3p"])
            obj (assoc-in acc [(v "infinitive") :useage mood tense] rs)]
        (if (obj "infinitive")
          obj
          (assoc-in obj  [(v "infinitive") :meta] (select-keys v ["infinitive"
                                     "infinitive_english"
                                     "verb_english"
                                     "gerund"
                                     "gerund_english"
                                     "pastparticiple"
                                     "pastparticiple_english"
                                     ])))
        ))
    {}
    verbs))

(def a (strip-stuff verbs))
(def c (create-thing verbs))

(spit "resources/verbs2.json" (generate-string c {:pretty true}))
