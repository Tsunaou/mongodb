#! /bin/bash
# without nemesis
# w1 local
lein run test -t causal-register --time-limit 721 -w majority -r majority --shard-count 3 --nodes-file /home/young/nodes --with-nemesis true --password disalg.root! --concurrency 10 --operation-counts 3000 --write-proportion 0.25 --read-proportion 0.75 --leave-db-running
#lein run test -t causal-register --time-limit 2400 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes --with-nemesis false --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.2 --read-proportion 0.8 --leave-db-running
#lein run test -t causal-register --time-limit 200 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes --with-nemesis false --password disalg.root! --concurrency 10 --operation-counts 10000 --write-proportion 0.8 --read-proportion 0.2 --leave-db-running
#lein run test -t causal-register --time-limit 2400 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes --with-nemesis false --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.6 --read-proportion 0.4 --leave-db-running
#lein run test -t causal-register --time-limit 2400 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes --with-nemesis false --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.4 --read-proportion 0.6 --leave-db-running
# majority majority
#lein run test -t causal-register --time-limit 2400 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis false --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.5 --read-proportion 0.5 --leave-db-running
#lein run test -t causal-register --time-limit 2400 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis false --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.2 --read-proportion 0.8 --leave-db-running
#lein run test -t causal-register --time-limit 2400 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis false --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.8 --read-proportion 0.2 --leave-db-running
#lein run test -t causal-register --time-limit 2400 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis false --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.4 --read-proportion 0.6 --leave-db-running
#lein run test -t causal-register --time-limit 2400 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis false --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.6 --read-proportion 0.4 --leave-db-running
# with nemesis
# w1 local
#lein run test -t causal-register --time-limit 2400 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.5 --read-proportion 0.5 --leave-db-running
#lein run test -t causal-register --time-limit 2400 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.2 --read-proportion 0.8 --leave-db-running
#lein run test -t causal-register --time-limit 2400 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.8 --read-proportion 0.2 --leave-db-running
#lein run test -t causal-register --time-limit 2400 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.4 --read-proportion 0.6 --leave-db-running
#lein run test -t causal-register --time-limit 2400 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.6 --read-proportion 0.4 --leave-db-running
# majority majority
#lein run test -t causal-register --time-limit 2400 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.5 --read-proportion 0.5 --leave-db-running
#lein run test -t causal-register --time-limit 2400 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.2 --read-proportion 0.8 --leave-db-running
#lein run test -t causal-register --time-limit 2400 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.8 --read-proportion 0.2 --leave-db-running
#lein run test -t causal-register --time-limit 2400 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.4 --read-proportion 0.6 --leave-db-running
#lein run test -t causal-register --time-limit 2400 -w majority -r majority --shard-count 2 --nodes-file /home/young/nodes --with-nemesis true --password XR.123456 --concurrency 10 --operation-counts 10000 --write-proportion 0.6 --read-proportion 0.4 --leave-db-running


