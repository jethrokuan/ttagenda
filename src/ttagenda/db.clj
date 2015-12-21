(ns ttagenda.db
  (:require [environ.core :refer [env]]
            [yesql.core :refer [defqueries]]))

(def db-spec {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname (or (System/getProperty "JDBC_CONNECTION_STRING")
                           (env :database-url))
              :user "tinkertanker"
              :password "tinkertanker"})

(defqueries "sql/queries.sql" {:connection db-spec})
