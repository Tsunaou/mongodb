(ns jepsen.mongodb.ycsb-generator
  (:require [clojure.tools.logging :refer [info]])
  (:import (site.ycsb.jepsen OKCounterNotInitializedException YCSBKeyValue YCSBGenerator)
           (java.util.concurrent.atomic AtomicInteger)))

(def default-max-op 10000)
(def ycsb-generator (atom (YCSBGenerator. default-max-op, 0.5, 0.5, "uniform", 100)))
(def max-op (atom default-max-op))
(def local-counter (atom (AtomicInteger. 0)))

(defn reset-ycsb-generator
  [max-op-counts rp wp distrib uniform_max]
  (reset! max-op max-op-counts)
  (reset! ycsb-generator (YCSBGenerator. max-op-counts, rp, wp, distrib, uniform_max)))

(defn init-ok-counter
  [^AtomicInteger counter]
  (info "init ok-counter in init-ok-counter with init value " (.get counter))
  (reset! local-counter counter)
  ;(.initOKCounter @ycsb-generator counter)
  )

(defn tuple
  "Constructs a kv tuple"
  [k v]
  (clojure.lang.MapEntry. k v))

(defn ycsb-overflow
  []
  (fn [_ _] {:type :invoke, :f :overflow, :value (tuple nil nil)}))

(defn ycsb-read
  [key]
  (fn [_ _] {:type :invoke, :f :read, :value (tuple key nil)}))

(defn ycsb-write
  [key value]
  (fn [_ _] {:type :invoke, :f :write, :value (tuple key value)}))

(defn ycsb-gen-java
  [index]
  (try
    (let [op (.nextOperation @ycsb-generator)
          key (.getKey op)
          value (.getValue op)]
      ;(println (str "index is " index ", key is " key ", value is " value ", (= key nil)" (= key nil) ", (= value nil)" (= value nil)))
      (if (or (= key nil) (>= (.get @local-counter) @max-op))
        (ycsb-overflow)
        (if (= value nil)
          (ycsb-read key)
          (ycsb-write key value))))
    (catch OKCounterNotInitializedException e
      (info e )))
)

(defn ycsb-gen
  "docstring"
  [number]
  (map ycsb-gen-java (range number)))