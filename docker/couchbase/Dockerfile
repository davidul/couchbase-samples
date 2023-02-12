FROM couchbase:community-7.1.1

COPY cluster-init.sh /

RUN chmod a+x /cluster-init.sh

ENTRYPOINT ["/cluster-init.sh"]