#! /bin/bash
# without nemesis
# w1 local
lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes --with-nemesis false --password XR.123456 --concurrency 10 --operation-counts 20000 --read-proportion 0.5 --write-proportion 0.5 --leave-db-running
lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes --with-nemesis false --password XR.123456 --concurrency 10 --operation-counts 20000 --read-proportion 0.2 --write-proportion 0.8 --leave-db-running
lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes --with-nemesis false --password XR.123456 --concurrency 10 --operation-counts 20000 --read-proportion 0.8 --write-proportion 0.2 --leave-db-running
# majority majority
lein run test -t causal-register --time-limit 600 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis false --password XR.123456 --concurrency 10 --operation-counts 20000 --read-proportion 0.5 --write-proportion 0.5 --leave-db-running
lein run test -t causal-register --time-limit 600 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis false --password XR.123456 --concurrency 10 --operation-counts 20000 --read-proportion 0.2 --write-proportion 0.8 --leave-db-running
lein run test -t causal-register --time-limit 600 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis false --password XR.123456 --concurrency 10 --operation-counts 20000 --read-proportion 0.8 --write-proportion 0.2 --leave-db-running
# with nemesis
# w1 local
lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 20000 --read-proportion 0.5 --write-proportion 0.5 --leave-db-running
lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 20000 --read-proportion 0.2 --write-proportion 0.8 --leave-db-running
lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 20000 --read-proportion 0.8 --write-proportion 0.2 --leave-db-running
# majority majority
lein run test -t causal-register --time-limit 600 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 20000 --read-proportion 0.5 --write-proportion 0.5 --leave-db-running
lein run test -t causal-register --time-limit 600 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 20000 --read-proportion 0.2 --write-proportion 0.8 --leave-db-running
lein run test -t causal-register --time-limit 600 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 20000 --read-proportion 0.8 --write-proportion 0.2 --leave-db-running


