(ns jepsen.mongodb.test-generator
  (:require [clojure.test :refer :all]
            [jepsen.generator :as generator]
            [jepsen.independent :as independent]
            [jepsen.mongodb.causal :as causal]
            [jepsen.mongodb.differentiated-generator :as diff]))

(def opts
  {:time-limit 100})


(defn get-op [{:keys [generator process ] :as test}]
  (let [op (generator/op-and-validate generator opts process)]
    (println (str "process is " process ", op is " op)))

  )

(def processes (range 30))

(defn gen-map [gen process]
  {:generator gen :process process})

(defn test-generator
  []
  (println "Test for generator")
  (let
    [gen (generator/seq (diff/gen-diff {:read-cnt 10, :write-cnt 20}))]
    ;[gen (->> (independent/concurrent-generator
    ;            5
    ;            (range 2)
    ;            (fn [k] (generator/seq causal/operations))))]
    [gen (:generator (causal/test opts))]
    (println gen)
    ;(loop [x 20]
    ;  (when (> x -1)
    ;    ;(println x)
    ;    (println (generator/op-and-validate gen opts x))
    ;    (recur (- x 1))))
    (println "Here")
    (let [test (map (partial gen-map gen) processes) ]
      (println test)
      (pmap get-op test)
      )
    )
  )

(test-generator)