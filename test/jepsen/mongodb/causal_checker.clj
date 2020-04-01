(ns jepsen.mongodb.causal-checker
  (:require [clojure.test :refer :all]
            [jepsen.checker :as checker]
            [jepsen.mongodb.adhoc-history :as his]
            [jepsen.mongodb.test-knossos :as tk]
            [jepsen.mongodb.mongo :as m]
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
          (println (knossos.op/Op->map op))
          (recur (rest his)))))))

(defn causal-check-process
  [history]
  (let [completed history]
    (loop [his completed]
      (if (empty? his)
        ;; We've checked every operation in the history
        {:valid? true}

        ;; checking checking checking...
        (let [op (knossos.op/Op->map (first his))]
          (when (integer? (:process op))
            (let [process (:process op)]
              (println (mod process 10)))
            )

          (recur (rest his)))))))

;(println (causal-check his/history))
;(println (causal-check tk/raw-history))
;(println (causal-check-process tk/raw-history))

(def concurrency (atom 1))
(def threads (atom (vec (range @concurrency))))
(def thread->session (atom (vec (take @concurrency (cycle [nil])))))                             ;存放线程对应client的causal session
(def clients-per-key (atom 1))

(def group-size (atom 1))                                   ; 每一组的进程数
(def group-count (atom 1))                                  ; 一共有多少组
(def group-threads (atom []))                               ; 每组对应的进程
(def group-keys (atom []))                                  ; 每组对应的key

(defn process->thread                                       ; 根据mod关系，得到process对应的线程
  [process]
  (mod process @concurrency))

(defn print-threads-session-status
  []
  (println "concurrency "  @concurrency)
  (println "threads " @threads)
  (println "thread->session " @thread->session)
  (println "clients-per-key " @clients-per-key)
  (println "group-size" @group-size)
  (println "group-count" @group-count)
  (println "group-threads" @group-threads)
  (println "group-keys" @group-keys))

(defn reset-concurrency
  [n]
  (reset! concurrency n)
  (reset! threads (vec (range @concurrency)))
  (reset! thread->session (vec (take @concurrency (cycle [nil])))))

(defn reset-clients-per-key
  [n]
  (reset! clients-per-key n)
  (reset! group-size n)
  (let [thread-count (count @threads)]
    (reset! group-count (quot thread-count @group-size)))
  (reset! group-threads (->> @threads
                             (partition @group-size)
                             (mapv vec)))
  (reset! group-keys (vec (take @group-count (cycle [nil])))))

(defn update-session
  [process session]
  (let [thread (process->thread process)]
    (reset! thread->session (assoc @thread->session thread session))))

(defn get-session
  [process]
  (let [thread (process->thread process)]
    (get @thread->session thread)))

(defn get-key-threads
  [process]
  (let [thread (process->thread process)
        group (quot thread @group-size)]
    (nth @group-threads group)))

(defn get-group-key
  [process]
  (let [thread (process->thread process)
        group (quot thread @group-size)]
    (nth @group-keys group)))

(defn set-group-key
  [process key]
  (let [thread (process->thread process)
        group (quot thread @group-size)]
    (reset! group-keys (assoc @group-keys group key))))

(defn syn-sessions
  [process]
  )

(println "Before reset!")
(print-threads-session-status)

(reset-concurrency 100)
(reset-clients-per-key 20)

(println "After reset!")
(print-threads-session-status)

;(loop [c-threads @threads]
;  (if (empty? c-threads)
;    ;; We've checked every operation in the history
;    {:valid? true}
;
;    ;; checking checking checking...
;    (let [current (first c-threads)
;          tmp-maps @thread->session]
;      (println current)
;      (reset! thread->session (assoc tmp-maps (str current) nil))
;      (println @thread->session)
;      (recur (rest c-threads)))))

(defn get-date []
  (.toString (java.util.Date.)))

;(doseq [thread (range @concurrency)]
;  (println (str "thread is " thread))
;  (update-session thread (str "session" thread (get-date)))
;  (println (get-session thread))
;  )
;
;
;(println @thread->session)
(doseq [x (range @concurrency)]
  (println (get-key-threads x)))

