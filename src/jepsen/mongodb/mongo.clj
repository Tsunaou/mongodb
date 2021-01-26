(ns jepsen.mongodb.mongo
  "MongoDB java driver adapter
   MongoDB java 驱动的适配器：用Clojure代码调用Java代码"
  (:refer-clojure :exclude [])
  (:require [clojure.core :as c]
            [clojure.tools.logging :refer :all]
            [jepsen.util :refer [timeout with-retry]])
  (:import (clojure.lang ExceptionInfo)
           (java.util ArrayList
                      List)
           (org.bson Document
                     BsonTimestamp
                     BsonDocument)
           (com.mongodb MongoClient
                        MongoClientOptions
                        ClientSessionOptions
                        ReadConcern
                        ReadPreference
                        ServerAddress
                        WriteConcern)
           (com.mongodb.client MongoCollection
                               MongoDatabase
                               MongoIterable)
           (com.mongodb.client.model Filters
                                     FindOneAndUpdateOptions
                                     ReturnDocument
                                     Sorts
                                     Updates
                                     UpdateOptions)
           (com.mongodb.client.result UpdateResult)
           (com.mongodb.session ClientSession)))

(defn ^MongoClientOptions default-client-options
  "MongoDB client options.
   用Java互操作返回一个MongoClientOptions对象，并设定一些参数"
  []
  (.build
   (doto (MongoClientOptions/builder)
     (.serverSelectionTimeout   2000)                       ;; Sets the server selection timeout in milliseconds, which defines how long the driver will wait for server selection to succeed before throwing an exception.
     (.maxWaitTime              120000)                      ;; Sets the maximum time that a thread will block waiting for a connection.
     (.connectTimeout           120000)                      ;; Sets the connection timeout.
     (.socketTimeout            60000))))                   ;; Sets the socket timeout.

(defn server-address
  "返回一个描述MongoDBServe的rAddress的对象"
  ([node]
   (ServerAddress. (name node)))

  ([node port]
   (ServerAddress. (name node) port)))

(defn client
  "Creates a new Mongo client.
   创建一个Mongo Client"
  ;FIXME:这里参数貌似不适配，但是貌似并不影响运行
  ([node]      (MongoClient. (server-address node) (default-client-options)))
  ([node port] (MongoClient. (server-address node port) (default-client-options))))

(defn cluster-client
  "Returns a mongoDB connection for all nodes in a test.
   对于test中的:nodes，通过->>和map函数创建5个MongoClient"
  [test]
  (MongoClient. (->> test :nodes (map server-address))
                (default-client-options)))

; TODO: what is causal session in mongodb
(defn start-causal-session [client]
  (info "Starting new session")
  (let [opts (-> (ClientSessionOptions/builder)
                 (.causallyConsistent true)
                 .build)]
    (.startSession client opts)))

(defn ^MongoDatabase db
  "Gets a Mongo database from a client."
  [^MongoClient client db]
  (.getDatabase client db))

(defn ^MongoCollection collection
  "Gets a Mongo collection from a DB."
  [^MongoDatabase db collection-name]
  (.getCollection db collection-name))

(def read-concerns
  "A map of read concern keywords to java driver constants."
  {:linearizable  ReadConcern/LINEARIZABLE
   :majority      ReadConcern/MAJORITY
   :local         ReadConcern/LOCAL
   :default       ReadConcern/DEFAULT})

(def write-concerns
  "A map of write concern keywords to java driver constants."
  {:acknowledged    WriteConcern/ACKNOWLEDGED
   :journaled       WriteConcern/JOURNALED
   :unacknowledged  WriteConcern/UNACKNOWLEDGED
   :w1              WriteConcern/W1
   :w2              WriteConcern/W2
   :w3              WriteConcern/W3
   :majority        WriteConcern/MAJORITY})

(defn ^MongoCollection with-read-concern
  "Returns a copy of the given collection, using the given read concern
  keyword."
  [^MongoCollection coll read-concern]
  (let [read-concern (c/get read-concerns read-concern)]
    (assert read-concern)
    (.withReadConcern coll read-concern)))                  ;.withReadConcern: Create a new MongoCollection instance with a different write concern.

(defn ^MongoCollection with-write-concern
  "Returns a copy of the given collection, using the given write concern
  keyword."
  [^MongoCollection coll write-concern]
  (let [write-concern (c/get write-concerns write-concern)]
    (assert write-concern)
    (.withWriteConcern coll write-concern)))                ;.withWriteConcern: Create a new MongoCollection instance with a different write concern.

(defn ^MongoClient enable-secondary-reads
  [^MongoClient client]
  (.slaveOk client))

(defn ^BsonTimestamp optime
  [^ClientSession session]
  (.getOperationTime session))

(defn ^BsonTimestamp operationTime
  [^ClientSession session]
  (.getOperationTime session))

(defn advanceOperationTime
  [^ClientSession session ^BsonTimestamp optime]
  (.advanceOperationTime session optime))

(defn ^BsonDocument clusterTime
  [^ClientSession session]
  (.getClusterTime session))

(defn advanceClusterTime
  [^ClientSession session ^BsonDocument clusterTime]
  (.advanceClusterTime session clusterTime))

(defn document
  "Creates a Mongo document from a map."
  [m]
  (reduce (fn [doc [k v]]
            (.append doc
                     (if (keyword? k) (name k) k)
                     (cond
                       (keyword?  v) (name v)
                       (map?      v) (document v)
                       (coll?     v) (ArrayList. (map document v))
                       true          v))
            doc)
          (Document.)
          m))

(defn document->map
  "Converts a document back into a map."
  [^Document doc]
  (when-not (nil? doc)
    (->> doc
         .entrySet
         (reduce (fn [m [k v]]
                   (assoc m
                          (keyword k)
                          (cond (instance? Document v) (document->map v)
                                (instance? List v)     (map document->map v)
                                true                   v)))
                 {}))))

(defn parse-result
  "Parses a command's result into a Clojure data structure."
  [doc]
  (document->map doc))

(defn run-command!
  "Runs an arbitrary command on a database. Command is a flat list of kv pairs,
  with the first pair being the command name, which will be transformed into a
  document. Includes a hardcoded 10 second timeout.
   执行数据库的任意命令，这里用&，command预计是(k1,v1,k2,v2,..)这样，所以要partition"
  [^MongoDatabase db & command]
  (->> command
       (partition 2)
       document
       (.runCommand db)
       parse-result))
;       (timeout 10000
;                (throw (ex-info "timeout" {:db (.getName db) :cmd command})))))

(defn admin-command!
  "Runs a command on the admin database."
  [client & command]
  (apply run-command! (db client "admin") command))


(defn find-one
  "Find a document by ID.
  If a session is provided first, will use that session for a
  causally consistent read"
  ;; TODO: 这里有causal相关了，但是session是啥呢
  ([^MongoCollection coll id]
   (-> coll
       (.find (Filters/eq "_id" id))
       .first
       document->map))

  ([^ClientSession session ^MongoCollection coll id]
   (-> coll
       (.find session (Filters/eq "_id" id))
       .first
       document->map)))


(defn update-result->map
  "Converts an update result to a clojure map."
  [^UpdateResult r]
  {:matched-count  (.getMatchedCount r)
   :modified-count (when (.isModifiedCountAvailable r)
                     (.getModifiedCount r))
   :upserted-id    (.getUpsertedId r)
   :acknowledged?  (.wasAcknowledged r)})

(defn upsert!
  "Ensures the existence of the given document, a map with at minimum an :_id
  key."
  ([^MongoCollection coll doc]
   (upsert! nil coll doc))

  ([^ClientSession session ^MongoCollection coll doc]
   (assert (:_id doc))
   (with-retry []
     (if session
       (-> coll
           (.replaceOne session
                        (Filters/eq "_id" (:_id doc))
                        (document doc)
                        (.upsert (UpdateOptions.) true))
           update-result->map)
       (-> coll
           (.replaceOne (Filters/eq "_id" (:_id doc))
                        (document doc)
                        (.upsert (UpdateOptions.) true))
           update-result->map))
     (catch com.mongodb.MongoWriteException e
       ;; This is probably
       ;; https://jira.mongodb.org/browse/SERVER-14322; we back off randomly
       ;; and retry.
       (if (= 11000 (.getCode e))
         (do (info "Retrying duplicate key collision")
             (Thread/sleep (rand-int 100))
             (retry))
         (throw e))))))
