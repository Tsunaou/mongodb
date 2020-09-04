(ns jepsen.mongodb.causal
  (:refer-clojure :exclude [test])
  (:require [jepsen
             [checker :as checker]
             [generator :as gen]
             [independent :as independent]]
            [jepsen.checker.timeline :as timeline]
            [knossos
             [op :as op]]
            [clojure.tools.logging :refer [info warn]]
            [clojure.pprint :refer [pprint]]
            [jepsen.mongodb.core :as core]
            [jepsen.mongodb.differentiated-generator :as diff]
            [jepsen.mongodb.ycsb-generator :as ycsb]))

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

;; Generators
(defn r   [_ _] {:type :invoke, :f :read})
(defn ri  [_ _] {:type :invoke, :f :read-init})
(defn cw1 [_ _] {:type :invoke, :f :write, :value 1})
(defn cw2 [_ _] {:type :invoke, :f :write, :value 2})
(defn cw3 [_ _] {:type :invoke, :f :write, :value 3})
(defn cw4 [_ _] {:type :invoke, :f :write, :value 4})
(defn cw5 [_ _] {:type :invoke, :f :write, :value 5})
(defn cw6 [_ _] {:type :invoke, :f :write, :value 6})
(defn cw7 [_ _] {:type :invoke, :f :write, :value 7})
(defn cw8 [_ _] {:type :invoke, :f :write, :value 8})
(defn cw9 [_ _] {:type :invoke, :f :write, :value 9})

(def operations
  [cw1 r cw2 r cw3 r cw4 r cw5 r cw6 r cw7 r cw8 r cw9 r])

(defn rx   [_ _] {:type :invoke, :f :read, :value (independent/tuple (rand-int 5) nil)})
(defn wx1 [_ _] {:type :invoke, :f :write, :value (independent/tuple (rand-int 5) (rand-int 5))})
(defn wx2 [_ _] {:type :invoke, :f :write, :value (independent/tuple (rand-int 5) (rand-int 5))})
(def xops [rx wx1 wx2])

;(def keys (atom (vec (shuffle (vec (range 20))))))
(def values (atom (vec (range 2000))))

(defn tuple
  "Constructs a kv tuple"
  [k v]
  (clojure.lang.MapEntry. k v))

;(let [queue (atom (vec (shuffle (vec (range 20)))))]
;  (dotimes [i (count @queue)]
;    (let [item (peek @queue)]
;      (println item)
;      (swap! queue pop))))

(defn next-value
  []
  (let [item (peek @values)]
    (swap! values pop)
    item))

(defn ycsb-read
  [key]
  (fn [_ _] {:type :invoke, :f :read, :value (tuple key nil)}))

(defn ycsb-write
  [key value]
  (fn [_ _] {:type :invoke, :f :write, :value (tuple key value)}))

(defn ycsb-gen
  [index]
  (let [type (rand-int 2)
        key (rand-int 20)]
    (println (str "index is " index))
    (case type
      0 (ycsb-read key)
      1 (let [value (next-value)]
          (println (str "value is " value))
          (ycsb-write key value)))))


(defn shard-migration-gen []
  (gen/seq (cycle [(gen/sleep 10)
                   {:type :info, :f :move}
                   (gen/sleep 0.5)
                   {:type :info, :f :move}
                   (gen/sleep 0.5)
                   {:type :info, :f :move}
                   (gen/sleep 0.5)
                   {:type :info, :f :start}
                   (gen/sleep 20)
                   {:type :info, :f :stop}
                   (gen/sleep 5)
                   {:type :info, :f :tempstop}
                   (gen/sleep 20)
                   {:type :info, :f :continue}])))

(defn shard-migration-gen-sleep []
  (gen/seq (cycle [(gen/sleep 30)])))

(defn test [opts]
  (let [clients-per-key (:clients-per-key opts)
        write-counts (:write-counts opts)
        read-counts (:read-counts opts)
        test-print (println (str "Clients-Per-Key " clients-per-key "wcnt " write-counts "rcnt " read-counts ))
        test-opts (info "test[opts]" opts)]
    {
     :clients-per-key clients-per-key
     :checker   (checker/compose
                  {:causal   (independent/checker (check (causal-register)))
                   :timeline (timeline/html)
                   :graph    (checker/latency-graph)
                   :perf     (checker/perf)
                   :clock    (checker/clock-plot)
                   :stats    (checker/stats)
                   :unhandled-exceptions (checker/unhandled-exceptions)
                   })
     ;:generator (->> (independent/concurrent-generator
     ;                  clients-per-key
     ;                  (range)
     ;                  (fn [k] (gen/seq (diff/gen-diff {:read-cnt (:read-counts opts), :write-cnt (:write-counts opts)}))))
     ;                (gen/stagger 1)
     ;                ;(gen/nemesis
     ;                ;  (gen/seq (cycle [(gen/sleep 10)
     ;                ;                   {:type :info, :f :start}
     ;                ;                   (gen/sleep 10)
     ;                ;                   {:type :info, :f :stop}])))
     ;                (gen/nemesis
     ;                  ;(shard-migration-gen)
     ;                  ; TODO:如果不作用nemesis，就用Sleeo
     ;                  (shard-migration-gen-sleep)
     ;                  )
     ;                (gen/time-limit (:time-limit opts)))
     :generator (->> (gen/seq (map ycsb-gen (range 2000)))
                     (gen/stagger 1)
                     ;(gen/nemesis
                     ;  (gen/seq (cycle [(gen/sleep 10)
                     ;                   {:type :info, :f :start}
                     ;                   (gen/sleep 10)
                     ;                   {:type :info, :f :stop}])))
                     (gen/nemesis
                       ;(shard-migration-gen)
                       ; TODO:如果不作用nemesis，就用Sleeo
                       (shard-migration-gen-sleep)
                       )
                     (gen/time-limit (:time-limit opts)))
     }
    ))
