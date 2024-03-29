# Organization
This project is organized into 3 main parts:
* `automation` - scripts to help setup things
* `docker` - how to run couchbase in docker
* `src` - samples code for couchbase

# Setup
Single node vanilla couchbase server can be started with:
```shell
docker/couchbase/single-node/start.sh
```
or
```shell
make vanilla
```
After server start go to [Couchbase server](http://localhost:8091/ui/index.html)

This Dockerfile is for multi node cluster: 
```shell
docker/couchbase/Dockerfile
```
It contains also the initialization of the cluster and bucket.

In `automation` are few scripts which can help setup things.
Although they are not necessary and everything can be done manually.
We will go over both approaches.

Prerequisite is you have `python` installed.
The `automation/setup.sh` will create python virtual environment. 
You need to source it afterward:
```shell
source aut-venv/bin/activate 
```

`setup.py` will build custom docker image. Script `start.py` will
execute the couchbase docker container and will setup the initial
cluster and bucket. Mainly useful during development when you 
need quick turnaround.

Go to [http://localhost:8091](http://localhost:8091) and login with
username `Administrator` and password `password`.

# Couchbase samples
Run vanilla couchbase server in docker
````
docker run -d --name db -p 8091-8094:8091-8094 -p 11210:11210 couchbase
````

Web console is available at http://localhost:8091/ui/index.html

## Cluster
Couchbase docker files are located at GitHub 
[https://github.com/couchbase/docker](https://github.com/couchbase/docker).
You can build your own image based on this one. 
Couchbase cluster is available as docker compose file. It starts 3 node cluster.
Admin console is available at standard location http://localhost:8091 .



## Samples
Most of the test are using [test containers](https://www.testcontainers.org/) , 
so no need to run standalone couchbase container.

