FROM couchbase:latest

COPY cluster-init.sh /

RUN chmod a+x /cluster-init.sh

ENTRYPOINT ["/cluster-init.sh"]