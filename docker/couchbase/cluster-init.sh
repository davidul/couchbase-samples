#!/bin/bash
set -m

# Enables error propagation
set -e

./entrypoint.sh couchbase-server &

check_db() {
  curl --silent http://127.0.0.1:8091/pools > /dev/null
  echo $?
}

# Variable used in echo
i=1
# Echo with
log() {
  echo "[$i] [$(date +"%T")] $@"
  i=`expr $i + 1`
}

until [[ $(check_db) = 0 ]]; do
  >&2 log "Waiting for Couchbase Server to be available ..."
  sleep 1
done

log "Init cluster"
couchbase-cli node-init -c 127.0.0.1 -u Administrator -p password
couchbase-cli cluster-init -c 127.0.0.1 --cluster-username Administrator --cluster-password password --services data,index,query --cluster-ramsize 2048 --cluster-index-ramsize 1024 --cluster-eventing-ramsize 512 --index-storage-setting default
couchbase-cli server-add -c 127.0.0.1:8091 --username Administrator --password password --server-add http://172.16.101.3:8091 --server-add-username Administrator --server-add-password password --services data,index,query
couchbase-cli server-add -c 127.0.0.1:8091 --username Administrator --password password --server-add http://172.16.101.4:8091 --server-add-username Administrator --server-add-password password --services data,index,query
couchbase-cli rebalance -c 127.0.0.1:8091 --username Administrator --password password
couchbase-cli bucket-create -c 127.0.0.1 --username Administrator --password password --bucket default --bucket-type couchbase --bucket-ramsize 1024

fg 1