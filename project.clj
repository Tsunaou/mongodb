(defproject jepsen.mongodb "0.2.1"
  :description "Jepsen MongoDB tests"
  :url "https://github.com/jepsen-io/mongodb"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [jepsen "0.1.8"]
                 [org.mongodb/mongodb-driver "3.4.2"]]
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
  :main jepsen.mongodb.runner
  :aot [jepsen.mongodb.runner
        clojure.tools.logging.impl])
