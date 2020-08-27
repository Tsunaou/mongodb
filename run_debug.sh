#! /bin/bash
lein run test -t causal-register --time-limit 600 -w majority -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 5 --leave-db-running
lein run test -t causal-register --time-limit 360 -w majority -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 10 --leave-db-running
lein run test -t causal-register --time-limit 360 -w majority -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 20 --leave-db-running
lein run test -t causal-register --time-limit 360 -w majority -r majority --shard-count 2  --nodes-file /home/young/nodes  --password XR.123456 --concurrency 100 --leave-db-running --write-counts 200 --read-counts 200 --clients-per-key 25 --leave-db-running

