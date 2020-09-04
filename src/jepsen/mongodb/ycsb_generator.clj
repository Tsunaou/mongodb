(ns jepsen.mongodb.ycsb-generator
  (:import (site.ycsb.jepsen YCSBGenerator)))

(def ycsb-generator (YCSBGenerator. 100000, 0.5, 0.5, "uniform", 100))

(defn tuple
  "Constructs a kv tuple"
  [k v]
  (clojure.lang.MapEntry. k v))

(defn ycsb-read
  [key]
  (fn [_ _] {:type :invoke, :f :read, :value (tuple key nil)}))

(defn ycsb-write
  [key value]
  (fn [_ _] {:type :invoke, :f :write, :value (tuple key value)}))

(defn ycsb-gen-java
  [index]
  (let [op (.nextOperation ycsb-generator)
        key (.getKey op)
        value (.getValue op)]
    ;(println (str "index is " index ", key is " key ", value is " value ", (= value nil)" (= value nil)))
    (if (= value nil)
      (ycsb-read key)
      (ycsb-write key value))))

(defn ycsb-gen
  "docstring"
  [number]
  (map ycsb-gen-java (range number)))