package davidul.basic;

import com.couchbase.client.java.kv.GetResult;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Get and lock the document
 */
public class GetAndLock {

    public static GetResult getAndLock(String connectionString, String id){
        return CouchbaseConnection
                .collection(connectionString)
                .getAndLock(id, Duration.ofSeconds(20));
    }

    public static Mono<GetResult> getAndLockReactive(String connectionString, String id){
        return CouchbaseConnection
                .collection(connectionString)
                .reactive()
                .getAndLock(id, Duration.ofSeconds(20));
    }
}
