package davidul.basic;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.client.java.kv.GetResult;
import reactor.core.publisher.Mono;

public class RetrieveData {

    public GetResult retrieveIfExist(String connectionString, String id) {
        final Collection collection = CouchbaseConnection.collection(connectionString);

        final ExistsResult exists = collection.exists(id);
        if (exists.exists()) {
            return collection.get(id);
        }
        return null;
    }

    public GetResult retrieve(String connectionString, String id){
        final Collection collection = CouchbaseConnection.collection(connectionString);
        return collection.get(id);
    }

    public void retrieveReactive(String connectionString, String id){
        final ReactiveCollection reactive = CouchbaseConnection.collection(connectionString).reactive();
        final Mono<GetResult> getResultMono = reactive.get(id);
    }
}
