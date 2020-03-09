(ns jepsen.mongodb.control
  "Utility functions.
   根据:virt的类型来来定义exec函数"
  (:require [jepsen.control :as c]
            [jepsen.control.local :as local]))

;;
;; exec methods
;;

;; 声明多重方法
(defmulti exec (fn [test & commands] (:virt test)))

;; 根据(:virt test)的值为多重方法提供实现
(defmethod exec :none [test & commands]
  (apply local/exec commands))

(defmethod exec :vm [test & commands]
  (apply c/exec commands))
