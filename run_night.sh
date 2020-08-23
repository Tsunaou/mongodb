#! /bin/bash
## w1 local
lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 5 --leave-db-running
lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 10 --leave-db-running
lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 20 --leave-db-running
lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 25 --leave-db-running
lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 50 --leave-db-running
## w1 majority
#lein run test -t causal-register --time-limit 600 -w w1 -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 5 --leave-db-running
#lein run test -t causal-register --time-limit 600 -w w1 -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 10 --leave-db-running
#lein run test -t causal-register --time-limit 600 -w w1 -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 20 --leave-db-running
#lein run test -t causal-register --time-limit 600 -w w1 -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 25 --leave-db-running
#lein run test -t causal-register --time-limit 600 -w w1 -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 50 --leave-db-running
## majority local
#lein run test -t causal-register --time-limit 600 -w majority -r local --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 5 --leave-db-running
#lein run test -t causal-register --time-limit 600 -w majority -r local --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 10 --leave-db-running
#lein run test -t causal-register --time-limit 600 -w majority -r local --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 20 --leave-db-running
#lein run test -t causal-register --time-limit 600 -w majority -r local --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 25 --leave-db-running
#lein run test -t causal-register --time-limit 600 -w majority -r local --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 50 --leave-db-running
## majority majority
lein run test -t causal-register --time-limit 600 -w majority -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 5 --leave-db-running
lein run test -t causal-register --time-limit 600 -w majority -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 10 --leave-db-running
lein run test -t causal-register --time-limit 600 -w majority -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 20 --leave-db-running
lein run test -t causal-register --time-limit 600 -w majority -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 25 --leave-db-running
mv store store0

lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 5 --leave-db-running
lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 10 --leave-db-running
lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 20 --leave-db-running
lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 25 --leave-db-running
lein run test -t causal-register --time-limit 600 -w majority -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 5 --leave-db-running
lein run test -t causal-register --time-limit 600 -w majority -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 10 --leave-db-running
lein run test -t causal-register --time-limit 600 -w majority -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 20 --leave-db-running
lein run test -t causal-register --time-limit 600 -w majority -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 25 --leave-db-running
mv store store1
