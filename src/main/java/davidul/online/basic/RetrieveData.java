package davidul.online.basic;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.client.java.kv.GetResult;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static davidul.online.basic.SimpleCouchbaseConnection.defaultCollection;

/**
 * Simple GET from couchbase
 *
 * @author ulicny.david@gmail.com
 */
public class RetrieveData {

    public static Optional<GetResult> retrieveIfExist(String connectionString, String documentId) {
        final Collection collection = defaultCollection(connectionString);

        final ExistsResult exists = collection.exists(documentId);
        if (exists.exists()) {
            return Optional.ofNullable(collection.get(documentId));
        }
        return Optional.empty();
    }

    public static JsonObject retrieve(String connectionString, String documentId) {
        return defaultCollection(connectionString)
                .get(documentId)
                .contentAsObject();
    }

    public static Mono<JsonObject> retrieveReactive(String connectionString, String documentId) {
        return defaultCollection(connectionString)
                .reactive()
                .get(documentId)
                .map(GetResult::contentAsObject);
    }
}
