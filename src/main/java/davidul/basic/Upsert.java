package davidul.basic;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutationResult;
import reactor.core.publisher.Mono;

public class Upsert {

    public static MutationResult upsert(String connectionString, String documentId, JsonObject document) {
        // get a collection reference
        Collection collection = CouchbaseConnection.collection(connectionString);
        return collection.upsert(documentId, document);
    }

    public static Mono<MutationResult> reactiveUpsert(String connectionString, String documentId, JsonObject document){
        final ReactiveCollection reactiveCollection = CouchbaseConnection.reactiveCollection(connectionString);
        return reactiveCollection.upsert(documentId, document);

    }
}
