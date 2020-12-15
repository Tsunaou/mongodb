(ns jepsen.mongodb.test-ycsb
  (:require [jepsen.mongodb.ycsb-generator :as ycsb])
  (:import (java.util.concurrent.atomic AtomicInteger)))

(def ok-counter
  (AtomicInteger. 0))

(ycsb/init-ok-counter ok-counter)

(let [op (ycsb/ycsb-gen-java 1)]
  (println (op nil nil)))

(let [op (ycsb/ycsb-gen-java 1)]
  (println (op nil nil)))

(let [op (ycsb/ycsb-gen-java 1)]
  (println (op nil nil)))

(let [ops (map ycsb/ycsb-gen-java (range 10))]
  (println ops))
