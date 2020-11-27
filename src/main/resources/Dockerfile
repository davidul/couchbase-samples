# Extend vert.x base image
FROM vertx/vertx3

ENV VERTICLE_JAR couchbase-samples-1.0-SNAPSHOT-fat.jar
ENV VERTICLE_HOME /usr/verticles
ENV VERTICLE_NAME davidul.complex.Main

EXPOSE 8080

# Copy the jar file into a container folder
COPY $VERTICLE_JAR $VERTICLE_HOME/

WORKDIR $VERTICLE_HOME

# Launch the verticle using 'java -jar verticles/vertx-kube-example-1.1.jar' command
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]