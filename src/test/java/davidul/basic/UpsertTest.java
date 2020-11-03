package davidul.basic;

import com.couchbase.client.core.cnc.EventBus;
import davidul.ContainerSetup;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.couchbase.CouchbaseService;
import org.testcontainers.utility.DockerImageName;

public class UpsertTest {

    private static String connectionString;

    @BeforeClass
    public static void setup(){
        connectionString = ContainerSetup.setup();
    }

    @Test
    public void upsert(){
        final EventBus eventBus = CouchbaseConnection.cluster(connectionString).environment().eventBus();
        eventBus.subscribe(e -> System.out.println(e));
        final Upsert upsert = new Upsert();
        upsert.upsert(connectionString);
    }
}
