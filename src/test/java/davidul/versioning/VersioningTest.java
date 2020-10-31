package davidul.versioning;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import davidul.basic.CouchbaseConnection;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.DockerHealthcheckWaitStrategy;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.couchbase.CouchbaseService;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

public class VersioningTest {

    private String id = "I::0001";

    @Test
    public void swap() throws IOException, InterruptedException {
        String command = "couchbase-cli cluster-init -c 0.0.0.0 " +
                "--cluster-username Administrator " +
                "--cluster-password password " +
                "--services data,index,query " +
                "--cluster-ramsize 512 n" +
                "--cluster-index-ramsize 256";


        final BucketDefinition aDefault = new BucketDefinition("default");
        final DockerImageName dockerImageName = DockerImageName.parse("couchbase").asCompatibleSubstituteFor("couchbase/server").withTag("latest");
        final CouchbaseContainer couchbaseContainer = new CouchbaseContainer(dockerImageName)
                .withBucket(aDefault)
                .withCredentials("Administrator", "password")
                .withEnabledServices(CouchbaseService.KV, CouchbaseService.INDEX, CouchbaseService.QUERY)

                ;
        couchbaseContainer.start();
        final String connectionString = couchbaseContainer.getConnectionString();

        //final String connectionString = couchbaseContainer.getConnectionString();
        final Collection collection = CouchbaseConnection.collection(connectionString);
        if(collection.exists(id).exists())
            collection.remove(id);

        final MutationResult upsert = collection.upsert(id, createObject("0"));
        final GetResult result1 = collection.get("I::0001");
        System.out.println(result1.contentAsObject());

        final Versioning versioning = new Versioning();
        final JsonObject newVersion = createObject("1");
        versioning.swap(id, newVersion);

        final GetResult result = collection.get(id);

        Assertions.assertThat(result.contentAsObject().get("version")).isEqualTo(2);

    }

    private static JsonObject createObject(String travels) {
        return JsonObject.create()
                .put("firstName", "David")
                .put("lastName", "Ulicny")
                .put("travels", travels)
                .put("version", 1);
    }
}
