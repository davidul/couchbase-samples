package davidul.basic;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import davidul.ContainerSetup;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

public class GetAndLockTest {

    private static String connectionString;
    private static final String ID_1 = "ID::1";

    @BeforeClass
    public static void setup(){
        connectionString = ContainerSetup.setup();
    }

    @Test
    public void _1(){
        final Upsert upsert = new Upsert();
        upsert.upsert(connectionString, ID_1);
        final GetAndLock getAndLock = new GetAndLock();
        final GetResult andLock = getAndLock.getAndLock(connectionString, ID_1);
        final Collection collection = CouchbaseConnection.collection(connectionString);
        final GetResult result = collection.get(ID_1);

        Assertions.assertThat(result.cas()).isEqualTo(-1);
        collection.unlock(ID_1, andLock.cas());
        final GetResult resultUnlocked = collection.get(ID_1);

        Assertions.assertThat(resultUnlocked.cas()).isNotEqualTo(-1);
    }

}
