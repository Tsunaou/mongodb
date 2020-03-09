#! /bin/bash
if [ $1 = 'causal-register' ]; then
  lein run test -t causal-register --time-limit 300 -w w1 -r local --shard-count 1  --nodes-file /home/young/nodes  --password XR.123456
elif [ $1 = 'sharded-register' ]; then
  lein run test -t sharded-register --time-limit 300 -w w1 -r local --shard-count 1  --nodes-file /home/young/nodes  --password XR.123456
elif [ $1 = 'sharded-set' ]; then
  lein run test -t sharded-set --time-limit 300 -w w1 -r local --shard-count 1  --nodes-file /home/young/nodes  --password XR.123456
fi

