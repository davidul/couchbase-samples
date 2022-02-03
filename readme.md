# Couchbase samples
Run vanilla couchbase server in docker
````
docker run -d --name db -p 8091-8094:8091-8094 -p 11210:11210 couchbase
````

Web console is available at http://localhost:8091/ui/index.html

## Cluster
Couchbase cluster is available as docker compose file. It starts 3 node cluster.
Admin console is available at standard location http://localhost:8091 .

## Couchbase connection
In simplest form just pass URL, username and password.
```java
Cluster.connect(connectionString, username, password);
```
where connection string is simple IP address or DNS record. Connection
string can be also coma separated list.

## Mutation
Couchbase supports insert, upsert, replace and delete operations to mutate
the content of the database.

### Insert
Insert operation accepts document id and the document itself.
If such document id already exists in database it throws an exception.

### Replace
Replace operation will swap the document, it will throw an exception
if the document already exists.

## Samples
Most of the test are using [test containers](https://www.testcontainers.org/) , 
so no need to run standalone couchbase container.

