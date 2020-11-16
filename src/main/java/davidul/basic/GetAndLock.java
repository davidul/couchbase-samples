package davidul.basic;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;

import java.time.Duration;

public class GetAndLock {

    public GetResult getAndLock(String connectionString, String id){
        final Collection collection = CouchbaseConnection.collection(connectionString);
        return collection.getAndLock(id, Duration.ofSeconds(20));
    }
}
