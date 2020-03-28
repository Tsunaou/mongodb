(ns jepsen.mongodb.differentiated-generator
  "负责生成满足Differentiated Histories的operations"
  (:require [clojure.test :refer :all]))

;; Generators
(defn ri  [_ _] {:type :invoke, :f :read-init})
(defn r   [_ _] {:type :invoke, :f :read})
(defn cw [_ _ value]
  (fn [_ _] {:type :invoke, :f :write, :value value}))


(defn random-pool
  "生成一个[1,2,..,pool-szie]打乱的写序列"
  [pool-size]
  (let [pool (range 1 (inc pool-size))]
     (shuffle pool)))

(defn gen-diff
  "负责生成满足Differentiated Histories的乱序operations"
  [{:keys [read-cnt write-cnt]}]
  (let [pools (random-pool write-cnt)
        reads (repeat read-cnt r)
        writes (map (partial cw nil nil) pools)]
    (shuffle (vec (concat reads writes)))
    ))

(gen-diff {:read-cnt 15, :write-cnt 15})

