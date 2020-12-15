(ns jepsen.mongodb.ycsb-generator
  (:require [clojure.tools.logging :refer [info]])
  (:import (site.ycsb.jepsen YCSBGeneratorWithCounter OKCounterNotInitializedException YCSBKeyValue)
           (java.util.concurrent.atomic AtomicInteger)))

(def ycsb-generator (atom (YCSBGeneratorWithCounter. 100000, 0.5, 0.5, "uniform", 100)))

(defn reset-ycsb-generator
  [max-op-counts rp wp distrib uniform_max]
  (reset! ycsb-generator (YCSBGeneratorWithCounter. max-op-counts, rp, wp, distrib, uniform_max)))

(defn init-ok-counter
  [^AtomicInteger counter]
  (info "init ok-counter in init-ok-counter with init value " (.get counter))
  (.initOKCounter @ycsb-generator counter))

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
      (if (= key nil)
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