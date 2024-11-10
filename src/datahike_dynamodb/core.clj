(ns datahike-dynamodb.core
  (:require [datahike.store :refer [empty-store delete-store connect-store default-config config-spec release-store store-identity]]
            [datahike.config :refer [map-from-env]]
            [konserve-dynamodb.core :as k]
            [clojure.spec.alpha :as s]))

(defmethod store-identity :dynamodb [store-config]
  [:dynamodb (:region store-config) (:table store-config)])

(defmethod empty-store :dynamodb [store-config]
  (k/connect-store store-config))

(defmethod delete-store :dynamodb [store-config]
  (k/delete-store store-config))

(defmethod connect-store :dynamodb [store-config]
  (k/connect-store store-config))

(defmethod default-config :dynamodb [config]
  (merge
   (map-from-env :datahike-store-config {:table "datahike"})
   config))

(s/def :datahike.store.dynamodb/backend #{:dynamodb})
(s/def :datahike.store.dynamodb/table string?)
(s/def :datahike.store.dynamodb/region string?)
(s/def :datahike.store.dynamodb/access-key string?)
(s/def :datahike.store.dynamodb/secret string?)
(s/def ::dynamodb (s/keys :req-un [:datahike.store.dynamodb/backend]
                    :opt-un [:datahike.store.dynamodb/region
                             :datahike.store.dynamodb/table
                             :datahike.store.dynamodb/access-key
                             :datahike.store.dynamodb/secret]))

(defmethod config-spec :dynamodb [_] ::dynamodb)

(defmethod release-store :dynamodb [_ store]
  (k/release store {:sync? true}))
