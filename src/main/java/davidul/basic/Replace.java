package davidul.basic;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutationResult;

public class Replace {

    public static MutationResult replace(String connectionString, String documentId, JsonObject document) {
        return CouchbaseConnection
                .collection(connectionString)
                .replace(documentId, document);
    }
}
