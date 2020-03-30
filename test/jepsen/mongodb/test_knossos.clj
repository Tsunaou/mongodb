(ns jepsen.mongodb.test-knossos
  (:require [clojure.test :refer :all]
            [knossos.cli :as kcli]
            [knossos.op :as kop]))

(def history-path "/home/young/Desktop/NJU-Bachelor/mongodb/store/mongo-causal-register_wc_:majority_rc_:majority_ti_100_sd_1_cry_10_se-wiredTiger_pv-1/20200329T205104.000+0800/independent/0/history.edn")

(defn get-history
  ([]
   (vec (map kop/Op->map (kcli/read-history history-path))))
  ([path]
   (vec (map kop/Op->map (kcli/read-history path)))))

(def raw-history
  (kcli/read-history history-path))




