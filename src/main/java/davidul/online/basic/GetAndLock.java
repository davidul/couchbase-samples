package davidul.online.basic;

import com.couchbase.client.java.kv.GetResult;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Get and lock the document
 */
public class GetAndLock {

    /**
     * Gets and lock for 20 seconds (max).
     *
     * @param connectionString
     * @param id
     * @return
     */
    public static GetResult getAndLock(String connectionString, String id){
        return CouchbaseConnection
                .collection(connectionString)
                .getAndLock(id, Duration.ofSeconds(20));
    }

    /**
     * Reactive version of get and lock, returns Mono
     * @param connectionString connection string
     * @param id document id
     * @return Mono
     */
    public static Mono<GetResult> getAndLockReactive(String connectionString, String id){
        return CouchbaseConnection
                .collection(connectionString)
                .reactive()
                .getAndLock(id, Duration.ofSeconds(20));
    }
}
