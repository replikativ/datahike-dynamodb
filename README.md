# Datahike dynamodb Backend

<p align="center">
<a href="https://clojurians.slack.com/archives/CB7GJAN0L"><img src="https://img.shields.io/badge/clojurians%20slack-join%20channel-blueviolet"/></a>
<a href="https://clojars.org/io.replikativ/datahike-dynamodb"> <img src="https://img.shields.io/clojars/v/io.replikativ/datahike-dynamodb.svg" /></a>
<a href="https://circleci.com/gh/replikativ/datahike-dynamodb"><img src="https://circleci.com/gh/replikativ/datahike-dynamodb.svg?style=shield"/></a>
<a href="https://github.com/replikativ/datahike-dynamodb/tree/main"><img src="https://img.shields.io/github/last-commit/replikativ/datahike-dynamodb/main"/></a>
</p>

The goal of this experimental backend is to support [DynamoDB](https://aws.amazon.com/dynamodb). DynamoDB is
has low latency and requires no dedicated running process for
reading, but has higher cost than using a local file system, S3 or a JDBC server as
backend. It is therefore optimal to store small databases that need to be updated and read quickly. 

## Configuration
Please read the [Datahike configuration docs](https://github.com/replikativ/datahike/blob/master/doc/config.md) on how to configure your backend. Details about the backend configuration can be found in [konserve-dynamodb](https://github.com/replikativ/konserve-dynamodb). Set your `AWS_ACCESS_KEY` and `AWS_SECRET_ACCESS_KEY` environment variables. A sample configuration for `create-database`, `connect` and `delete-database` is (you can optionally pass the keys in the config, but this is only recommended for development and testing):
```clojure
{:store {:backend :dynamodb
         :table "my-datahike-db"
         :region "us-west-1"
         :access-key "YOUR_ACCESS_KEY"
         :secret "YOUR_ACCESS_KEY_SECRET"}}
```
This same configuration can be achieved by setting one environment variable for the dynamodb backend
and one environment variable for the configuration of the dynamodb backend:
```bash
DATAHIKE_STORE_BACKEND=dynamodb
DATAHIKE_STORE_CONFIG='{:table "datahike-dynamodb-instance" ...}'
```

## Usage
Add to your Leiningen or Boot dependencies:
[![Clojars Project](https://img.shields.io/clojars/v/io.replikativ/datahike-dynamodb.svg)](https://clojars.org/io.replikativ/datahike-dynamodb)

Now require the Datahike API and the datahike-dynamodb namespace in your editor or REPL using the
keyword `:dynamodb`. If you want to use other backends than dynamodb please refer to the official
[Datahike docs](https://github.com/replikativ/datahike/blob/master/doc/config.md).

### Run Datahike in your REPL
```clojure
  (ns project.core
    (:require [datahike.api :as d]
              [datahike-dynamodb.core]))

  (def cfg {:store {:backend :dynamodb
                    :region "us-west-1"
                    :table "my-datahike-db"}})

  ;; Create a database at this place, by default configuration we have a strict
  ;; schema validation and keep historical data
  (d/create-database cfg)

  (def conn (d/connect cfg))

  ;; The first transaction will be the schema we are using:
  (d/transact conn [{:db/ident :name
                     :db/valueType :db.type/string
                     :db/cardinality :db.cardinality/one }
                    {:db/ident :age
                     :db/valueType :db.type/long
                     :db/cardinality :db.cardinality/one }])

  ;; Let's add some data and wait for the transaction
  (d/transact conn [{:name  "Alice", :age   20 }
                    {:name  "Bob", :age   30 }
                    {:name  "Charlie", :age   40 }
                    {:age 15 }])

  ;; Search the data
  (d/q '[:find ?e ?n ?a
         :where
         [?e :name ?n]
         [?e :age ?a]]
    @conn)
  ;; => #{[3 "Alice" 20] [4 "Bob" 30] [5 "Charlie" 40]}

  ;; Clean up the database if it is not needed any more
  (d/delete-database cfg)
```

## Run Tests

```bash
  bash -x ./bin/run-integration-tests
```

## License

Copyright © 2024 Christian Weilbach

This program and the accompanying materials are made available under the terms of the Eclipse Public License 1.0.
