#!/bin/bash
set -m

# Enables error propagation
set -e

./entrypoint.sh couchbase-server &

check_db() {
  curl --silent http://127.0.0.1:8091/pools > /dev/null
  echo $?
}

check_cluster(){
  couchbase-cli server-list -c ${MASTER} -u Administrator -p password > /dev/null
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
  sleep 5
done


log "Init node"
couchbase-cli node-init -c ${HOSTNAME} -u Administrator -p password

if [ -z "$MASTER" ]
 then
   log "Init cluster"
    couchbase-cli cluster-init -c master --cluster-username Administrator \
      --cluster-password password --services data,index,query \
      --cluster-ramsize 512 \
      --cluster-index-ramsize 512 \
      --cluster-eventing-ramsize 512 \
      --index-storage-setting default
fi

if [ ! -z "$MASTER" ]
  then
  until [[ $(check_cluster) = 0 ]]; do
    >&2 log "Waiting for cluster ..."
    sleep 1
  done

  ipaddress=$(ifconfig eth0 | grep -w inet | tr -s ' ' |  cut -d' ' -f3)

  couchbase-cli server-add -c ${MASTER} \
     --username Administrator \
     --password password \
     --server-add http://${ipaddress}:8091 \
     --server-add-username Administrator \
     --server-add-password password \
     --services data,index,query

  couchbase-cli rebalance -c ${HOSTNAME}:8091 --username Administrator --password password
fi

#couchbase-cli server-add -c 127.0.0.1:8091 --username Administrator --password password --server-add http://172.16.101.4:8091 --server-add-username Administrator --server-add-password password --services data,index,query


if [ -z "$MASTER" ]
then
  log "Create bucket"
couchbase-cli bucket-create \
  -c ${HOSTNAME} \
  --username Administrator \
  --password password \
  --bucket default \
  --bucket-type couchbase \
  --bucket-ramsize 128
fi

fg 1