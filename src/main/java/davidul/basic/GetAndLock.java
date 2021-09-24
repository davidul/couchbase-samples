package davidul.basic;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.kv.GetResult;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class GetAndLock {

    public static GetResult getAndLock(String connectionString, String id){
        final Collection collection = CouchbaseConnection.collection(connectionString);

        return collection.getAndLock(id, Duration.ofSeconds(20));
    }

    public static Mono<GetResult> getAndLockReactive(String connectionString, String id){
        final ReactiveCollection reactive = CouchbaseConnection.collection(connectionString).reactive();
        return reactive.getAndLock(id, Duration.ofSeconds(20));
    }
}
