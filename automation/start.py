import docker
import requests
import time

client = docker.from_env()

container_name = "single-node-cb"

containers = client.containers.list(all=True) #stopped as well
for c in containers:
    print("Container " + c.name)
    if c.name == container_name:
        print("Stop and remove " + c.name)
        c.stop()
        c.remove()
        break


couch = client.containers.run(image="couchbase:latest",
                              ports={'8091/tcp':8091, '8092/tcp':8092, '8093/tcp':8093, '8094/tcp':8094, '11210/tcp':11210},
                              detach=True,
                              name=container_name)
print("Container ID " + couch.id)

time.sleep(10)
count = 0
check_db = True
while check_db and count < 4:
    count += 1
    response = requests.get("http://127.0.0.1:8091/pools")
    if response.status_code == 200:
        break
    else:
        time.sleep(5)


result = couch.exec_run(cmd="couchbase-cli cluster-init -c 127.0.0.1 --cluster-username Administrator "
                   "--cluster-password password "
                   "--services data,index,query "
                   "--cluster-ramsize 2048 "
                   "--cluster-index-ramsize 1024 "
                   "--cluster-eventing-ramsize 512 "
                   "--index-storage-setting default")

print(result[0])
print(result[1])

result = couch.exec_run(cmd="couchbase-cli bucket-create -c 127.0.0.1 "
                   "--username Administrator "
                   "--password password "
                   "--bucket default "
                   "--bucket-type couchbase "
                   "--bucket-ramsize 1024")

print(result[0])
print(result[1])