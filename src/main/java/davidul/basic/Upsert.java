package davidul.basic;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutationResult;
import reactor.core.publisher.Mono;

public class Upsert {

    public static MutationResult upsert(String connectionString, String documentId, JsonObject document) {
        // get a collection reference
        return CouchbaseConnection
                .collection(connectionString)
                .upsert(documentId, document);
    }

    public static Mono<MutationResult> reactiveUpsert(String connectionString, String documentId, JsonObject document) {
        return CouchbaseConnection
                .reactiveCollection(connectionString)
                .upsert(documentId, document);
    }
}
