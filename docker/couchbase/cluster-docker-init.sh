docker exec -it db1 couchbase-cli cluster-init -c 127.0.0.1 --cluster-username Administrator --cluster-password password --services data,index,query --cluster-ramsize 2048 --cluster-index-ramsize 1024 --cluster-eventing-ramsize 512 --index-storage-setting default
docker exec -it db1 couchbase-cli server-add -c 172.16.101.2:8091 --username Administrator --password password --server-add http://172.16.101.3:8091 --server-add-username Administrator --server-add-password password --services data,index,query
docker exec -it db1 couchbase-cli server-add -c 172.16.101.2:8091 --username Administrator --password password --server-add http://172.16.101.4:8091 --server-add-username Administrator --server-add-password password --services data,index,query
docker exec -it db1 couchbase-cli rebalance -c 172.16.101.2:8091 --username Administrator --password password

docker exec -it db1 couchbase-cli bucket-create -c 172.16.101.2:8091 --username Administrator --password password --bucket default --bucket-type couchbase --bucket-ramsize 1024