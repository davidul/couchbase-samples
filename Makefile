VANILLA_NAME = single-node-cb
COUCHBASE_IMAGE = couchbase:community-7.2.0
COUCHBASE_CLUSTER_IMAGE = couchbase-cluster

vanilla-up:
	docker/couchbase/single-node/start.sh $(VANILLA_NAME) $(COUCHBASE_IMAGE)

vanilla-stop:
	docker stop $(VANILLA_NAME)

vanilla-rm:
	docker rm $(VANILLA_NAME)

vanilla-image-rm:
	docker rmi $(COUCHBASE_IMAGE)

cluster-build:
	docker build -t $(COUCHBASE_CLUSTER_IMAGE) --build-arg COUCHBASE_IMAGE=$(COUCHBASE_IMAGE) docker/couchbase

cluster-up: cluster-build
	docker compose -f docker/couchbase/multi-node/docker-compose-cbs-only.yml up -d

