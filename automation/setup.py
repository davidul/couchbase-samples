import docker

client = docker.from_env()

tag_name = "davidul-couchbase"

client.images.remove(image=tag_name)

img: tuple = client.images.build(path="../docker/couchbase", tag=tag_name)
print(img[0].id)

for x in img[1]:
    print(x)


# client.images.remove()
#
# for x in client.containers.list(True):
#     print(x.name + " - " + x.status)


