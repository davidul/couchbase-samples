package davidul.online.basic.mutation;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutationResult;
import davidul.online.connection.SimpleCouchbaseConnection;

/**
 * Replaces the document.
 *
 */
public class Replace {

    public static MutationResult replace(String connectionString, String documentId, JsonObject document) {
        return SimpleCouchbaseConnection
                .defaultCollection(connectionString)
                .replace(documentId, document);
    }
}
