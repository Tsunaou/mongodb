#! /bin/bash
lein run test -t causal-register --time-limit 600 -w w1 -r local --shard-count 2 --nodes-file /home/young/nodes  -ne true --password XR.123456 --concurrency 100 --operation-counts 20000 --read-proportion 0.5 --write-proportion 0.5 --leave-db-running
