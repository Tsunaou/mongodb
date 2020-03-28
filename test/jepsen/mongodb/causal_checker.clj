(ns jepsen.mongodb.causal-checker
  (:require [clojure.test :refer :all]
            [jepsen.checker :as checker]
            [jepsen.mongodb.adhoc-history :as his]
            [knossos.op :as op]
            ))



(defprotocol Model
  (step [model op]))

(defrecord Inconsistent [msg]
  Model
  (step [this op] this)

  Object
  (toString [this] msg))

(defn inconsistent
  "Represents an invalid termination of a model; e.g. that an operation could
  not have taken place."
  [msg]
  (Inconsistent. msg))

(defn inconsistent?
  "Is a model inconsistent?"
  [model]
  (instance? Inconsistent model))

(defrecord CausalRegister [value counter last-pos]
  Model
  (step [r op]
    (let [c (inc counter)
          v'   (:value op)
          pos  (:position op)
          link (:link op)]
      (if-not (or (= link :init)
                  (= link last-pos))
        (inconsistent (str "Cannot link " link
                           " to last-seen position " last-pos))
        (condp = (:f op)
          :write (cond
                   ;; Write aligns with next counter, OK
                   (= v' c)
                   (CausalRegister. v' c pos)

                   ;; Attempting to write an unknown value
                   (not= v' c)
                   (inconsistent (str "expected value " c
                                      " attempting to write "
                                      v' " instead")))

          :read-init  (cond
                        ;; Read a non-0 value from a freshly initialized register
                        (and (=    0 counter)
                             (not= 0 v'))
                        (inconsistent (str "expected init value 0, read " v'))

                        ;; Read the expected value of the register,
                        ;; update the last known position
                        (or (nil? v')
                            (= value v'))
                        (CausalRegister. value counter pos)

                        ;; Read a value that we haven't written
                        true (inconsistent (str "can't read " v'
                                                " from register " value)))

          :read  (cond
                   ;; Read the expected value of the register,
                   ;; update the last known position
                   (or (nil? v')
                       (= value v'))
                   (CausalRegister. value counter pos)

                   ;; Read a value that we haven't written
                   true (inconsistent (str "can't read " v'
                                           " from register " value)))))))
  Object
  (toString [this] (pr-str value)))

(defn causal-register []
  (CausalRegister. 0 0 nil))

(defn check
  "A series of causally consistent (CC) ops are a causal order (CO). We issue a CO
  of 5 read (r) and write (w) operations (r w r w r) against a register (key).
  All operations in this CO must appear to execute in the order provided by
  the issuing site (process). We also look for anomalies, such as unexpected values"
  [model]
  (reify checker/Checker
    (check [this test history opts]
      (let [completed (filter op/ok? history)]
        (loop [s model
               history completed]
          (if (empty? history)
            ;; We've checked every operation in the history
            {:valid? true
             :model s}

            ;; checking checking checking...
            (let [op (first history)
                  s' (step s op)]
              (if (inconsistent? s')
                {:valid? false
                 :error (:msg s')}
                (recur s' (rest history))))))))))



;(defn causal-check
;  [model]
;  (reify checker/Checker
;    (check [checker test history opts]
;      (let [completed (filter op/ok? history)]
;
;        ))))

(defn causal-check
  [history]
  (let [completed (filter op/ok? history)]
    (loop [his completed]
      (if (empty? his)
        ;; We've checked every operation in the history
        {:valid? true}

        ;; checking checking checking...
        (let [op (first his)]
          (println op)
          (recur (rest his)))))))


(println (causal-check his/history))
