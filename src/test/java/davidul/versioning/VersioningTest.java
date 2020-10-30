package davidul.versioning;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import davidul.basic.CouchbaseConnection;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.couchbase.CouchbaseService;
import org.testcontainers.utility.DockerImageName;

public class VersioningTest {

    private String id = "I::0001";

    @Test
    public void swap() {
       /* final BucketDefinition aDefault = new BucketDefinition("default");
        final DockerImageName dockerImageName = DockerImageName.parse("couchbase").asCompatibleSubstituteFor("couchbase/server").withTag("latest");
        final CouchbaseContainer couchbaseContainer = new CouchbaseContainer(dockerImageName)
                .withBucket(aDefault)
                .withCredentials("Administrator", "Administrator")
                .withEnabledServices(CouchbaseService.KV, CouchbaseService.INDEX, CouchbaseService.QUERY);
        couchbaseContainer.start();

        final String connectionString = couchbaseContainer.getConnectionString();*/
        final Collection collection = CouchbaseConnection.collection();
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
