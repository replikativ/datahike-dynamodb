(ns datahike-dynamodb.core-test
  (:require
   #?(:cljs [cljs.test    :as t :refer-macros [is deftest]]
      :clj  [clojure.test :as t :refer        [is deftest]])
   [datahike.api :as d]
   [datahike-dynamodb.core]))

(deftest ^:integration test-dynamodb
  (let [config {:store {:backend :dynamodb
                        :region  "us-west-2"
                        :table   "datahike-dynamodb-test"}
                :schema-flexibility :read
                :keep-history? false}
        _ (d/delete-database config)]
    (is (not (d/database-exists? config)))
    (Thread/sleep 10000)
    (let [_ (d/create-database config)
          _ (Thread/sleep 10000)
          conn (d/connect config)]

      (d/transact conn [{:db/id 1, :name  "Ivan", :age   15}
                        {:db/id 2, :name  "Petr", :age   37}
                        {:db/id 3, :name  "Ivan", :age   37}
                        {:db/id 4, :age 15}])
      (is (= (d/q '[:find ?e :where [?e :name]] @conn)
             #{[3] [2] [1]}))

      (d/release conn)
      (is (d/database-exists? config))
      (d/delete-database config)
      (Thread/sleep 10000)
      (is (not (d/database-exists? config))))))
