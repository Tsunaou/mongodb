(defproject jepsen.mongodb "0.2.3"
  :description "Jepsen MongoDB tests"
  :url "https://github.com/Tsunaou/mongodb"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  ;:plugins [[lein-localrepo "0.5.4"]]
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.cli "0.4.1"]
                 [jepsen "0.1.17"]
                 [org.mongodb/mongodb-driver "3.6.3"]]
  :jvm-opts ["-Xmx16g"
             "-Xms16g"
             "-Xmn4g"
             "-XX:+UseConcMarkSweepGC"
             "-XX:+UseParNewGC"
             "-XX:+CMSParallelRemarkEnabled"
             "-XX:+AggressiveOpts"
             "-XX:+UseFastAccessorMethods"
             "-XX:MaxInlineLevel=32"
             "-XX:MaxRecursiveInlineLevel=2"
             "-server"]
  :resource-paths ["resources/core-0.18.0-SNAPSHOT.jar"]
  :main jepsen.mongodb.runner
  :aot [jepsen.mongodb.runner
        clojure.tools.logging.impl])
