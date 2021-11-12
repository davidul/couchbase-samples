package davidul.versioning;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import davidul.ContainerSetup;
import davidul.online.connection.SimpleCouchbaseConnection;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

public class VersioningTest {

    private String id = "I::0001";
    private static String connectionString;

    @BeforeClass
    public static void setup(){
        connectionString = ContainerSetup.setup();
    }

    @Test
    public void swap() {
        String command = "couchbase-cli cluster-init -c 0.0.0.0 " +
                "--cluster-username Administrator " +
                "--cluster-password password " +
                "--services data,index,query " +
                "--cluster-ramsize 512 n" +
                "--cluster-index-ramsize 256";

        final Collection collection = SimpleCouchbaseConnection.defaultCollection(connectionString);
        if(collection.exists(id).exists())
            collection.remove(id);

        final MutationResult upsert = collection.upsert(id, createObject("0"));
        final GetResult result1 = collection.get("I::0001");

        final Versioning versioning = new Versioning();
        final JsonObject newVersion = createObject("1");
        versioning.swap(id, newVersion);

        final GetResult result = collection.get(id);

        Assertions.assertThat(result.contentAsObject().get("version")).isEqualTo(2);
    }

    //@Test
    public void swap_fail(){
        final Collection collection = SimpleCouchbaseConnection.defaultCollection(connectionString);
        if(collection.exists(id).exists())
            collection.remove(id);

        final MutationResult upsert = collection.upsert(id, createObject("0"));
        collection.upsert("I::0001::v1", createObject("0"));

        final Versioning versioning = new Versioning();
        final JsonObject newVersion = createObject("1");
        versioning.swap(id, newVersion);

        final GetResult result = collection.get(id);
        Assertions.assertThat(result.contentAsObject().get("version")).isEqualTo(1);
    }

    private static JsonObject createObject(String travels) {
        return JsonObject.create()
                .put("firstName", "David")
                .put("lastName", "Ulicny")
                .put("travels", travels)
                .put("version", 1);
    }
}
