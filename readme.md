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

