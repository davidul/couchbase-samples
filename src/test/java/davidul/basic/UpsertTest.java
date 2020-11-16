package davidul.basic;

import com.couchbase.client.core.cnc.EventBus;
import davidul.ContainerSetup;
import org.junit.BeforeClass;
import org.junit.Test;

public class UpsertTest {

    private static String connectionString;
    private static final String ID_1 = "ID::1";

    @BeforeClass
    public static void setup(){
        connectionString = ContainerSetup.setup();
    }

    @Test
    public void upsert(){
        final EventBus eventBus = CouchbaseConnection.cluster(connectionString).environment().eventBus();
        eventBus.subscribe(e -> System.out.println(e));
        final Upsert upsert = new Upsert();
        upsert.upsert(connectionString,  ID_1);
    }
}
