package davidul.basic;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.client.java.kv.GetResult;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static davidul.basic.CouchbaseConnection.collection;

/**
 * Simple GET from couchbase
 *
 * @author ulicny.david@gmail.com
 */
public class RetrieveData {

    public static Optional<GetResult> retrieveIfExist(String connectionString, String documentId) {
        final Collection collection = collection(connectionString);

        final ExistsResult exists = collection.exists(documentId);
        if (exists.exists()) {
            return Optional.ofNullable(collection.get(documentId));
        }
        return Optional.empty();
    }

    public static JsonObject retrieve(String connectionString, String documentId) {
        return collection(connectionString)
                .get(documentId)
                .contentAsObject();
    }

    public static Mono<JsonObject> retrieveReactive(String connectionString, String documentId) {
        return collection(connectionString)
                .reactive()
                .get(documentId)
                .map(GetResult::contentAsObject);
    }
}
