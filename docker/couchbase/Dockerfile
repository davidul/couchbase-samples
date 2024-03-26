ARG COUCHBASE_IMAGE
FROM $COUCHBASE_IMAGE

COPY cluster-init.sh /

RUN chmod a+x /cluster-init.sh

ENTRYPOINT ["/cluster-init.sh"]