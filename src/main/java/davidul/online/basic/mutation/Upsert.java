package davidul.online.basic.mutation;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutationResult;
import davidul.online.connection.SimpleCouchbaseConnection;
import reactor.core.publisher.Mono;

public class Upsert {

    public static MutationResult upsert(String connectionString, String documentId, JsonObject document) {
        // get a collection reference
        return SimpleCouchbaseConnection
                .defaultCollection(connectionString)
                .upsert(documentId, document);
    }

    public static Mono<MutationResult> reactiveUpsert(String connectionString, String documentId, JsonObject document) {
        return SimpleCouchbaseConnection
                .reactiveCollection(connectionString)
                .upsert(documentId, document);
    }
}
