(ns jepsen.mongodb.test-ycsb
  (:require [jepsen.mongodb.ycsb-generator :as ycsb]))


(let [op (ycsb/ycsb-gen-java 1)]
  (println (op nil nil)))

(let [op (ycsb/ycsb-gen-java 1)]
  (println (op nil nil)))

(let [op (ycsb/ycsb-gen-java 1)]
  (println (op nil nil)))