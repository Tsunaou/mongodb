(ns jepsen.mongodb.test-session
  (:require [clojure.test :refer :all]))

(def concurrency (atom 1))
(def processes (atom (vec (range @concurrency))))

(defn reset-concurrency
  [n]
  (reset! concurrency n)
  (reset! processes (vec (range @concurrency))))

(defn process->thread                                       ; 根据mod关系，得到process对应的线程
  [process]
  (mod process @concurrency))

(defn thread->process
  [thread]
  (get @processes thread))

(defn update-process
  [thread new-process]
  (reset! processes (assoc @processes thread new-process)))

(reset-concurrency 100)
(println @processes)

(dotimes [n 5]
  (println (str "Round " n))
  (println "Before " (thread->process 0))
  (update-process 0 (rand-int 50))
  (println "After " (thread->process 0))
  (println "--------------------------------------------"))

(if (not (= 5 1))
  (println "not equal"))

(def session (atom nil))
(def last-optime (atom nil))

(reset! session nil)
(if (nil? @session)
  (let [_   (println (str "Update session" @session))
        new-session (rand-int 10)
        _   (reset! session new-session)    ;为当前的client开启一个causal session
        _   (reset! last-optime nil)]))
(reset! session nil)
(if (nil? @session)
  (let [_   (println (str "Update session" @session))
        new-session (rand-int 10)
        _   (reset! session new-session)    ;为当前的client开启一个causal session
        _   (reset! last-optime nil)]))