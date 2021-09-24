package davidul.basic;

import com.couchbase.client.core.error.AmbiguousTimeoutException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import davidul.ContainerSetup;
import davidul.basic.sampledata.SampleData;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static davidul.basic.GetAndLock.getAndLock;
import static davidul.basic.GetAndLock.getAndLockReactive;
import static davidul.basic.Upsert.upsert;
import static org.assertj.core.api.Assertions.assertThat;

public class GetAndLockTest {

    private static String connectionString;
    private static final String ID_1 = "ID::1";
    private static final String ID_2 = "ID::2";

    @BeforeClass
    public static void setup(){
        connectionString = ContainerSetup.setup();
        upsert(connectionString, ID_1);
        upsert(connectionString, ID_2);
    }

    @Test
    public void _1(){
        final GetResult andLock = getAndLock(connectionString, ID_1);

        final Collection collection = CouchbaseConnection.collection(connectionString);
        final GetResult result = collection.get(ID_1);

        //data are locked, cas == -1
        assertThat(result.cas()).isEqualTo(-1);

        collection.unlock(ID_1, andLock.cas());
        final GetResult resultUnlocked = collection.get(ID_1);

        assertThat(resultUnlocked.cas()).isNotEqualTo(-1);
    }

    @Test
    public void reactive(){
        final Mono<GetResult> andLock1 = getAndLockReactive(connectionString, ID_2);
        final Mono<GetResult> andLock2 = getAndLockReactive(connectionString, ID_2);

        StepVerifier
                .create(andLock1.map(GetResult::contentAsObject))
                .expectNext(SampleData.sample())
                .verifyComplete();

        StepVerifier
                .create(andLock2.map(GetResult::cas))
                .expectError(AmbiguousTimeoutException.class)
                .verify();
    }

}
