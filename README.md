# Jepsen MongoDB tests

Runs two types of tests against a MongoDB cluster: single-document compare-and-set, and document-per-element set insertion.

## Configuration
### Import Jar which is not in Maven Repository
Use a plugin named [lein-localrepo](https://github.com/kumarshantanu/lein-localrepo).

In this project, we need the YCSB core `core-0.18.1-SNAPSHOT.jar`with our `ycsb-generator`.
After packaging it, we put it to `./resources/` and we can import this jar by commands as follows:

```bash
lein localrepo coords ./resources/core-0.18.1-SNAPSHOT.jar 
----------------------------------------------------------------
WARNING: You have $CLASSPATH set, probably by accident.
It is strongly recommended to unset this before proceeding.
./resources/core-0.18.1-SNAPSHOT.jar core/core 0.18.1-SNAPSHOT
```
Then edit `project.clj`
```clj
:dependencies [...
             [core/core "0.18.1-SNAPSHOT"]]
```
Then set the classpath and add jar to local maven repository
```bash
lein localrepo install ./resources/core-0.18.1-SNAPSHOT.jar core/core 0.18.1-SNAPSHOT
```
At last, solve the dependency problems
```bash
lein deps
```
## Examples

```sh
# Usage
lein run
lein run test --help

# Short set test with write and read concern majority
lein run test -t set

# 100 second linearizability test with write concern "journaled" and "local" read concern
lein run -t register --time-limit 100 -w journaled -r local

# 120 second sharded-set test with write concern "w1" and "local" read concern, 2 shards, and 4 mongos routers
lein run -t sharded-set --time-limit 120 -w w1 -r local --shard-count 2 --mongos-count 4

# 300 second causal-register test with write concern "w1" and "local" read concern and one shard
lein run -t causal-register --time-limit 300 -w w1 -r local --shard-count 1

# Use the mmapv1 storage engine
lein run test -t register -s mmapv1

# Pick a different tarball to install
lein run test -t register --tarball https://...foo.tar.gz
```

## Building and running as a single jar

```sh
lein uberjar
java -jar target/jepsen.mongodb-0.2.0-SNAPSHOT-standalone.jar test ...
```

## License

Copyright © 2015, 2016 Kyle Kingsbury & Jepsen, LLC

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
