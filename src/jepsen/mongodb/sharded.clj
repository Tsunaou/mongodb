(ns jepsen.mongodb.sharded
  "MongoDB tests against a sharded cluster. Including sets,
  CAS register,and causal register.
   对MongoDB在分片集群下的测试"
  (:refer-clojure :exclude [test])
  (:require [jepsen.mongodb
             [core :as core]
             [dbutil :as mdbutil]
             [control :as mcontrol]
             [cluster :as mc]
             [util :as mu]
             [time :as mt]
             [mongo :as m]
             [causal :as causal]
             ]
            [jepsen
             [control :as c]
             [client  :as client]
             [checker :as checker]
             [db      :as db]
             [generator :as gen]
             [independent :as independent]
             [nemesis :as nemesis]
             [util      :as util :refer [meh
                                         timeout
                                         relative-time-nanos]]]
            ;[jepsen.tests.causal :as causal]
            [jepsen.nemesis.combined :as combined]
            [jepsen.nemesis.time :as ne-time]
            [jepsen.tests.causal-reverse :as causal-reverse]
            [jepsen.checker.timeline :as timeline]
            [jepsen.control.util :as cu]
            [jepsen.os.debian :as debian]
            [clj-time.core :as time]
            [clj-time.local :as time.local]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.logging :refer [info]]
            [knossos.model :as model]
            [jepsen.tests :as tests])
  (:import [java.util.concurrent Semaphore
            TimeUnit]))

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

(defn init-configsvr! [test node]
  (c/sudo (:username test)
          (mcontrol/exec test
                         :echo (-> "mongod-configsvr.conf" io/resource slurp
                                   (str/replace #"%PATH_PREFIX%" (mu/path-prefix test node)))
                         :> (mu/path-prefix test node "/mongod-configsvr.conf"))))

(defn init-shardsvr! [test node idx repl-set]
  (c/sudo (:username test)
          (mcontrol/exec test
                         :echo (-> (str "mongod-shardsvr.conf") io/resource slurp
                                   (str/replace #"%ENABLE_MAJORITY_READ_CONCERN%"
                                                (str (= (:read-concern test) :majority)))
                                   (str/replace #"%PATH_PREFIX%" (mu/path-prefix test node))
                                   (str/replace #"%REPL_SET%" repl-set)
                                   (str/replace #"%DB_PATH%" (str "data" idx))
                                   (str/replace #"%STORAGE_ENGINE%" (:storage-engine test)))
                         :> (mu/path-prefix test node (str "/mongod-shardsvr" idx ".conf")))))

(defn format-configsvrs [nodes]
  (str/join "," (map #(str (name %) ":27019") nodes)))

(defn init-mongos! [test node]
  (c/sudo (:username test)
          (mcontrol/exec test
                         :echo (-> "mongos.conf" io/resource slurp
                                   (str/replace #"%PATH_PREFIX%" (mu/path-prefix test node))
                                   (str/replace #"%CONFIGSVRS%" (format-configsvrs (:nodes test))))
                         :> (mu/path-prefix test node "/mongos.conf"))))

(defn start-daemon!
  [clock test node {:keys [pidfile process-name port configfile]}]
  (apply mc/start-daemon! test
         {:chdir (mu/path-prefix test node)
          :background? false
          :logfile (mu/path-prefix test node "/stdout.log")
          :make-pidfile? false
          :match-executable? false
          :match-process-name? true
          :pidfile (mu/path-prefix test node pidfile)
          :process-name process-name}
         (conj (mt/wrap! clock test (mu/path-prefix test node (str "/bin/" process-name)))
               :--fork
               :--pidfilepath (mu/path-prefix test node pidfile)
               :--port port
               :--config (mu/path-prefix test node configfile))))

;; TODO Not compatible outside :vm. The mongo.util stop/kill-daemon is not working in this context
;       for reasons I haven't been able to figure out yet.
(defn kill-all! [test node]
  (let [shard-count (:shard-count test)]
    (cu/stop-daemon! (mu/path-prefix test node "/mongos.pid"))
    (cu/stop-daemon! (mu/path-prefix test node "/mongod-configsvr.pid"))
    (doseq [idx (range 0 shard-count)]
      (cu/stop-daemon! (mu/path-prefix test node (str "/mongod-shardsvr" idx ".pid"))))))

(defn db
  "在特定url的节点，上搭建 MongoDB Sharded Cluster"
  [clock url {:keys [mongos-sem chunk-size shard-count]}]
  (let [state (atom {})]
    (reify db/DB
      (setup! [_ test node]                                 ; Set up the database on this particular node.
        (swap! state assoc node {:setup-called true})       ; 表明 node 已经准备启动 state: {node {:setup-called true}}
        (util/timeout 300000
                      (throw (RuntimeException.
                              (str "Mongo setup on " node " timed out!")))

                      (when (= :vm (:virt test))
                        (debian/install [:libc++1 :libsnmp30]))

                      ;; Install MongoDB Package
                      (->> (or (some->> (:mongodb-dir test)
                                        io/file
                                        .getCanonicalPath
                                        (str "file://"))
                               url)
                           (mdbutil/install! test node))

                      ;; TODO: 接下来与非分片的数据库搭建就不同了
                      ;; 阶段一 Create the Config Server Replica Set
                      ;; Setup configsvr replset
                      ;; Start each member of the config server replica set
                      (init-configsvr! test node)
                      ;; net.port: 27019 if mongod is a config server member
                      (start-daemon! clock test node {:pidfile "/mongod-configsvr.pid"
                                                      :process-name "mongod"
                                                      :port 27019
                                                      :configfile "/mongod-configsvr.conf"})
                      ;; 加入节点，直到primary节点对其他所有节点可见
                      (core/join! test node {:port 27019 :repl-set-name "configsvr"})

                      ;; 阶段二 Start a mongos for the Sharded Cluster
                      ;; Nodes spawn a router for the setup phase.
                      (init-mongos! test node)
                      ;; net.port: 27017 for mongod (if not a shard member or a config server member) or mongos instance
                      (start-daemon! clock test node {:pidfile "/mongos.pid"
                                                      :process-name "mongos"
                                                      :port 27017
                                                      :configfile "/mongos.conf"})

                      ;; 阶段三 Init and Add Shards to the Cluster
                      ;; Setup `shard-count` shardsvr replsets
                      (doseq [idx (range 0 shard-count)]
                        ;; Port starts at 27020
                        (let [port (+ 27020 idx)
                              ;; Each replset gets its own name by its index
                              repl-set (str "jepsen" idx)
                              filename (str "/mongod-shardsvr" idx)]
                          (mt/init! clock test)
                          (init-shardsvr! test node idx repl-set)
                          (start-daemon! clock test node {:pidfile (str filename ".pid")
                                                          :process-name "mongod"
                                                          :port port
                                                          :configfile (str filename ".conf")})
                          (core/join! test node {:port port :repl-set-name repl-set})

                          ;; Add shard to cluster with the most recently
                          (m/admin-command! (m/client node 27017)
                                            :addShard (str "jepsen" idx "/" node ":" port))))

                      ;; 阶段四 配置集群
                      ;; Configure the cluster
                      (let [conn (m/client node 27017)
                            coll (m/collection (m/db conn "config") "settings")]
                        ;; Wrapped with meh to swallow errors from repeated calls. It's not
                        ;; really what we want but ok.
                        ;; Enable Sharding for a Database
                        (util/meh (m/admin-command! conn :enableSharding "jepsen"))
                        (util/meh (m/admin-command! conn
                                                    :shardCollection "jepsen.sharded"
                                                    :key {:_id 1}))

                        ;; Set chunk size, defaults to 64MB
                        (try
                          (m/upsert! coll {:_id "chunksize" :value chunk-size})
                          (catch com.mongodb.MongoCommandException e
                            (when-not (re-matches #"duplicate key error" (.getMessage e))
                              (throw e))))

                        ;; Nodes race to acquire `--mongos-count` locks. If they do not acquire a
                        ;; lock, they tear down the router they used for setup, just like a
                        ;; game of musical chairs.
                        (when-not (.tryAcquire mongos-sem)
                          (cu/stop-daemon! (mu/path-prefix test node "/mongos.pid")))))
        (info (str (name node) " Setup OK!"))
        (info (str "sessions " (:sessions test)))
        ;; TODO: 在这里预处理causal session相关的数据结构
        )

      (teardown! [_ test node]
        (if-not (:setup-called (get @state node))
          (kill-all! test node)
          (do (mdbutil/snarf-logs! test node)
              (try
                (kill-all! test node)
                (finally (mdbutil/snarf-logs! test node)))))))))

;; Client for Set
(defrecord Client [db-name coll-name read-concern write-concern client coll]
  client/Client
  (open! [this test node]
    (let [client (m/client node)
          coll   (-> client
                     (m/db db-name)
                     (m/collection coll-name)
                     (m/with-read-concern read-concern)
                     (m/with-write-concern write-concern))]
      (assoc this :client client :coll coll)))

  (invoke! [this test op]
    (core/with-errors op #{:read}
      (case (:f op)
        :add (let [res (m/insert! coll {:value (:value op)})]
               (reset! (:last-op-id test) (:value op))
               (assoc op :type :ok))
        :read (assoc op
                     :type :ok
                     :value (->> coll
                                 m/find-all
                                 (map :value)
                                 (into (sorted-set)))))))

  (close! [this test]
    (.close ^java.io.Closeable client))

  (setup! [_ _])
  (teardown! [_ _]))

(defn set-client
  [opts]
  (Client. "jepsen" "sharded"
           (:read-concern opts)
           (:write-concern opts)
           nil nil))

;; Client for Register
(defrecord RegisterClient [db-name
                           coll-name
                           read-concern
                           write-concern
                           read-with-find-and-modify
                           client
                           coll]
  client/Client
  (open! [this test node]
    (let [client (m/client node)
          coll   (-> client
                     (m/db db-name)
                     (m/collection coll-name)
                     (m/with-read-concern read-concern)
                     (m/with-write-concern write-concern))]
      (assoc this :client client :coll coll)))

  (invoke! [this test op]
    (core/with-errors op #{:read}
      (let [id    (key (:value op))
            value (val (:value op))]
        (reset! (:last-op-id test) id)
        (case (:f op)
          :read (let [doc (if read-with-find-and-modify
                            ;; CAS read
                            (m/read-with-find-and-modify coll id)
                            ;; Normal read
                            (m/find-one coll id))]
                  (assoc op
                         :type  :ok
                         :value (independent/tuple id (:value doc))))

          :write (let [res (m/upsert! coll {:_id id, :value value})]
                   ;; Note that modified-count could be zero, depending on the
                   ;; storage engine, if you perform a write the same as the
                   ;; current value.
                   (assert (< (:matched-count res) 2))
                   (assoc op :type :ok))

          :cas   (let [[value value'] value
                       res (m/cas! coll
                                   {:_id id, :value value}
                                   {:_id id, :value value'})]
                   ;; Check how many documents we actually modified.
                   (condp = (:matched-count res)
                     0 (assoc op :type :fail)
                     1 (assoc op :type :ok)
                     true (assoc op :type :info
                                 :error (str "CAS: matched too many docs! "
                                             res))))))))

  (close! [this test]
    (.close ^java.io.Closeable client))

  (setup! [_ _])
  (teardown! [_ _]))

(defn register-client [opts]
  (RegisterClient. "jepsen" "sharded"
           (:read-concern opts)
           (:write-concern opts)
           (:read-with-find-and-modify opts)
           nil nil))

(defn maybe-conn
  "Tries to connect to a router. Returns the conn or nil."
  [node]
  (let [conn (util/meh (m/client node))
        ;; Check to see if we have a valid connection
        test (util/meh (m/admin-command! conn :listCollections 1))]
    (when-not (or (instance? Exception conn)
                  (instance? Exception test))
      conn)))

(defn get-routers
  "Attempts to connect to each node, returning a single router conn."
  [nodes]
  (->> nodes
       (map maybe-conn)
       (remove nil?)))

(defn balancer-nemesis [conns]
  (reify nemesis/Nemesis
    (setup! [this test]
      (balancer-nemesis (get-routers (:nodes test))))

    (invoke! [this test op]
        (case (:f op)
          :move (let [dest-replset (str "jepsen" (rand-int (:shard-count test)))
                      id @(:last-op-id test)]
                  (assert (not (empty? conns)) "Nemesis is unable to connect a mongos router.")
                  (m/admin-command! (rand-nth conns)
                                    :moveChunk "jepsen.sharded"
                                    :find {:_id id}
                                    :to dest-replset)
                  (assoc op :value [:moving-chunk-with id :to dest-replset]))))))

(defn sharded-nemesis []
  (nemesis/compose
   {#{:start :stop} (nemesis/partition-random-halves)
    #{:move} (balancer-nemesis nil)}))

(defn start!
  "Starts DB."
  [node test]
  (info node "starting node")
  (let [cdb (:db test)]
    (cu/signal! "mongo" :CONT)
    ))


(defn stop!
  "Stops DB."
  [node test]
  (info node "stopping node")
  (let [cdb (:db test)]
    (cu/signal! "mongo" :STOP)
    ))

(defn node-tempstop-continue
  [targeter start! stop!]
  (let [nodes (atom nil)]
    (reify nemesis/Nemesis
      (setup! [this test] this)

      (invoke! [this test op]
        (locking nodes
          (assoc op :type :info, :value
                    (case (:f op)
                      :continue (let [ns (:nodes test)
                                   ns (try (targeter test ns)
                                           (catch clojure.lang.ArityException e
                                             (targeter ns)))
                                   ns (util/coll ns)]
                               (if ns
                                 (if (compare-and-set! nodes nil ns)
                                   (c/on-many ns (start! test c/*host*))
                                   (str "nemesis already disrupting "
                                        (pr-str @nodes)))
                                 :no-target))
                      :tempstop (if-let [ns @nodes]
                              (let [value (c/on-many ns (stop! test c/*host*))]
                                (reset! nodes nil)
                                value)
                              :not-started)))))

      (teardown! [this test]))))

(defn killer
  "Kills a random node on start, restarts it on stop."
  []
  (node-tempstop-continue
    rand-nth
    (fn start [test node] (stop! node test))
    (fn stop  [test node] (start! node test))))

(defn sharded-nemesis_node []
  (nemesis/compose
    {#{:start :stop} (nemesis/partition-random-halves)
     #{:continue :tempstop} (killer)
     #{:move} (balancer-nemesis nil)}))

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
                   {:type :info, :f :stop}])))

(defn ensure-shard-count [opts]
  (assert
   (<= 1 (:shard-count opts))
   "Sharded tests must be run with a --shard-count of 1 or higher"))

(defn set-test
  "Tests against a sharded mongodb cluster. We insert documents against
  the mongos router while inducing shard migrations and partitioning the
  network."
  [opts]
  (ensure-shard-count opts)
  (let [mongos-sem (Semaphore. (or (:mongos-count opts) (count (:nodes opts))))]
    (core/mongodb-test
     "sharded-set"
     (merge
      opts
      {:client (set-client opts)
       :nemesis (sharded-nemesis)
       :last-op-id (atom nil)
       :generator (gen/phases
                   (->> (range)
                        (map (fn [x] {:type :invoke, :f :add, :value x}))
                        gen/seq
                        (gen/stagger 1/2)
                        (gen/nemesis (shard-migration-gen))
                        (gen/time-limit (:time-limit opts)))
                   (gen/nemesis
                    (gen/once {:type :info, :f :stop, :value nil}))
                   (gen/sleep 40)
                   (gen/clients (gen/each
                                 (gen/limit 2 {:type :invoke, :f :read, :value nil}))))
       :db (db (:clock opts)
               (:tarball opts)
               {:mongos-sem  mongos-sem
                :chunk-size  (:chunk-size opts)
                :shard-count (:shard-count opts)})
       :checker (checker/compose
                 {:set (checker/set)
                  :timeline (timeline/html)
                  :perf (checker/perf)})}))))

;; Generators
(defn r   [_ _] {:type :invoke, :f :read})
(defn w   [_ _] {:type :invoke, :f :write, :value (rand-int 5)})
(defn cas [_ _] {:type :invoke, :f :cas, :value [(rand-int 5) (rand-int 5)]})

(defn register-test
  "Tests against a sharded mongodb cluster. We insert documents against
  the mongos router while inducing shard migrations and partitioning the
  network."
  [opts]
  (ensure-shard-count opts)
  (let [mongos-sem (Semaphore. (or (:mongos-count opts) (count (:nodes opts))))]
    (core/mongodb-test
     "sharded-register"
     (merge
      opts
      {:client (register-client opts)
       :nemesis (sharded-nemesis)
       :last-op-id (atom nil)
       :generator (->> (independent/concurrent-generator
                        10
                        (range)
                        (fn [k]
                          (->> (gen/mix [w cas cas])
                               (gen/reserve 5 (if (:no-reads opts)
                                                (gen/mix [w cas cas])
                                                r))
                               (gen/time-limit (:key-time-limit opts)))))
                       (gen/stagger 1)
                       (gen/nemesis (shard-migration-gen))
                       (gen/time-limit (:time-limit opts)))
       :db (db (:clock opts)
               (:tarball opts)
               {:mongos-sem  mongos-sem
                :chunk-size  (:chunk-size opts)
                :shard-count (:shard-count opts)})
       :checker (checker/compose
                 {:linear  (independent/checker (checker/linearizable (model/cas-register)))
                  :timeline (independent/checker (timeline/html))
                  :perf     (checker/perf)})}))))

;;  用于Jepsen Causal测试的Client,实现了Client协议，有5阶段的生命周期
(defrecord CausalClient [db-name
                         coll-name
                         read-concern
                         write-concern
                         secondary-ok?
                         client
                         coll
                         session
                         last-optime]
  client/Client
  (open! [this test node]                                   ; 获得绑定到特定节点的客户端的副本
    (let [client (m/client node)
          client (if secondary-ok?                          ; 在connect=direct模式下，驱动会自动找寻主服务器. 在connect=replicaSet 模式下，驱动仅仅连接主服务器，并且所有的读写命令都连接到主服务器。
                   (m/enable-secondary-reads client)
                   client)
          coll   (-> client
                     (m/db db-name)
                     (m/collection coll-name)
                     (m/with-read-concern  read-concern)
                     (m/with-write-concern write-concern))]
      (assoc this
             :client      client
             :coll        coll
             :session     (atom nil)
             :last-optime (atom nil))))

  (invoke! [this test op]                                   ;调用操作并返回结果
    (core/with-errors op #{read}
      (let [id    (key (:value op))
            value (val (:value op))]
        (info jepsen.generator/*threads*)
        (let [process (:process op)
              key-threads (get-key-threads process)]
          (info (str "key threads " process " " key-threads))
          ;; 当前组的key的绑定
          (let [last-key (get-group-key process)]
            ;; 初始化或更新key
            (info "last-key is " last-key)
            (if (or (nil? last-key) (not (= last-key id)))
              (let [_ (reset! session nil)
                    _ (set-group-key process id)]
                (doseq [x-threads key-threads]
                  (update-session x-threads nil)))))
          ;; causal session 的创建
          (if (or (nil? @session) (nil? (get-session process)))
            ;; 若当前client没有session，则为其创建一个session
            (let [new-session (m/start-causal-session client)
                  _   (update-session process new-session)
                  _   (reset! session new-session)]       ;为当前的client开启一个causal session
              ))
          ; session 的输出
          ;(doseq [thread key-threads]
          ;  (let [session (get-session thread)]
          ;    (if (nil? session)
          ;      (info (str "thread " thread " session is none"))
          ;      (info (str "thread " thread " " session)))))
          ;; causal session 的同步
          ;; TODO: ~~我觉得这里可能会死锁~~(根据MongoDB的文档，不需要手动同步)
          ;(doseq [friend-process key-threads]
          ;  (if-not (= process friend-process)
          ;    (let [friend-session (get-session friend-process)]
          ;      (if-not (nil? friend-session)
          ;        (let [friend-optime (m/operationTime friend-session)
          ;              friend-clustertime (m/clusterTime friend-session)]
          ;          ;(info (str "optime " friend-optime))
          ;          ;(info (str "clustertime " friend-clustertime))
          ;          ;(info (str "session " @session))
          ;          (m/advanceOperationTime @session friend-optime)
          ;          (m/advanceClusterTime @session friend-clustertime))))))

              )
        ;(case (:f op)
        ;  :read-init ()
        ;  :read ()
        ;  :write ()
        ;  )
        (case (:f op)
          :read-init (let [doc (m/find-one @session coll id)
                           ;; Set the value to 0 (init value for BEGH checker)
                           ;; if read returns nil.
                           v   (or (:value doc) 0)
                           ;; Turn this into something comparable
                           optime (-> (m/optime @session) .getValue)
                           ;; Update test state with latest optime for key/session
                           _ (reset! last-optime optime)]
                       (assoc op
                              :type  :ok
                              :value (independent/tuple id v)
                              :position optime
                              :link :init))

          :read (let [doc (m/find-one @session coll id)
                      v   (or (:value doc) 0)
                      optime (-> (m/optime @session) .getValue)
                      ;TODO: 这里的last-optime为什么不更新？
                      lo @last-optime
                      _ (reset! last-optime optime)]
                  (assoc op
                         :type  :ok
                         :value (independent/tuple id v)
                         :position optime
                         :link lo))

          :write (let [res (m/upsert! @session coll {:_id id, :value value})
                       optime (-> (m/optime @session) .getValue)
                       lo @last-optime
                       _ (reset! last-optime optime)]
                   ;; Note that modified-count could be zero, depending on the
                   ;; storage engine, if you perform a write the same as the
                   ;; current value.
                   (assert (< (:matched-count res) 2))
                   (assoc op
                          :type :ok
                          :position optime
                          :link lo))))))

  (close! [_ _]
    (.close ^java.io.Closeable client))

  (setup! [_ _])
  (teardown! [_ _]))

(defn causal-client [opts]
  (CausalClient. "jepsen"
                 "causal-register"
                 (:read-concern opts)
                 (:write-concern opts)
                 (:secondary-ok? opts)
                 nil
                 nil
                 nil
                 nil))

(defn update-concurrency-per-key
  [concur c-per-key]
  (info "not equal, update")
  (reset-concurrency concur)
  (reset-clients-per-key c-per-key)
  (print-threads-session-status)
  )

(defn prepare-threads
  [opts]
  (let [concur (:concurrency opts)
        concur-before @concurrency
        c-per-key (:clients-per-key opts)
        node "control node"]
    (info (str "on node" node " after"))
    (if (= concur concur-before)
      (info "equal")
      (update-concurrency-per-key concur c-per-key))
    )
  )

;; Causal相关的测试
;; opts a map like {:read-concern :concurrency etc.}
(defn causal-test [opts]
  (ensure-shard-count opts)
  (info "opts in causal-test" opts)                      ;; TODO: 研究怎么格式化打印（而不是打印到一行里）
  (info "causal/test opts" (causal/test opts))
  (info "others" {:concurrency (count (:nodes opts))
                  :client (causal-client opts)
                  ;:nemesis (nemesis/partition-random-halves)
                  :os debian/os
                  :db (db (:clock opts)                                ;  TODO: 这个db是个啥
                          (:tarball opts)
                          {
                           :chunk-size  (:chunk-size opts)
                           :shard-count (:shard-count opts)})})
  (prepare-threads opts)
  (let [mongos-sem (Semaphore. (or (:mongos-count opts)     ;; 好像没有看到代码里哪里有 mongos-count
                                   (count (:nodes opts))))] ;; Semahphore Java信号量
    (core/mongodb-test
     "causal-register"
     (merge
      opts
      (causal/test opts)
      {
       ;:concurrency (count (:nodes opts))
       :client (causal-client opts)
       ;:nemesis (sharded-nemesis)
       :nemesis (sharded-nemesis_node)
       :os debian/os
       :db (db (:clock opts)
               (:tarball opts)
               {:mongos-sem  mongos-sem
                :chunk-size  (:chunk-size opts)
                :shard-count (:shard-count opts)})}))))
