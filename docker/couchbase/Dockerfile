FROM couchbase:community-7.1.0

COPY cluster-init.sh /

RUN chmod a+x /cluster-init.sh

ENTRYPOINT ["/cluster-init.sh"]