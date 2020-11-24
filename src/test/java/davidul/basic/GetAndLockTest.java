package davidul.basic;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import davidul.ContainerSetup;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.publisher.Mono;

public class GetAndLockTest {

    private static String connectionString;
    private static final String ID_1 = "ID::1";
    private static final String ID_2 = "ID::2";

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
        /*try {
            final GetResult andLock1 = getAndLock.getAndLock(connectionString, ID_1);
        }catch (Exception e){
            e.printStackTrace();
        }*/
        final Collection collection = CouchbaseConnection.collection(connectionString);
        final GetResult result = collection.get(ID_1);

        Assertions.assertThat(result.cas()).isEqualTo(-1);
        collection.unlock(ID_1, andLock.cas());
        final GetResult resultUnlocked = collection.get(ID_1);

        Assertions.assertThat(resultUnlocked.cas()).isNotEqualTo(-1);
    }

    @Test
    public void reactive(){
        final Upsert upsert = new Upsert();
        upsert.upsert(connectionString, ID_2);
        final GetAndLock getAndLock = new GetAndLock();
        final Mono<GetResult> andLock1 = getAndLock.getAndLockReactive(connectionString, ID_2);
        final Mono<GetResult> andLock2 = getAndLock.getAndLockReactive(connectionString, ID_2);

        andLock1.subscribe(getResult -> System.out.println("Lock1 " + getResult.cas()), ex -> ex.printStackTrace());
        andLock2.subscribe(getResult -> System.out.println("Lock2 " + getResult.cas()), ex -> ex.printStackTrace());
        System.out.println("");
        final List<Integer> map = List.range(1, 10000000).map(f -> f * f);
    }

    @Test
    public void inTrx(){
        final Upsert upsert = new Upsert();
        upsert.upsert(connectionString, ID_1);
        final GetAndLock getAndLock = new GetAndLock();
        getAndLock.getAndLockInTrx(connectionString, ID_1);
    }


}
