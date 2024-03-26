#!/bin/bash

set -ex
echo "Starting Couchbase Server"
echo "Cluster: $1"
echo "Image: $2"

docker run -d --name $1 -p 8091-8094:8091-8094 -p 11210:11210 $2