package davidul.basic;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;

public class Replace {

    public static void replace(String connectionString, String documentId, JsonObject document){
        final Collection collection = CouchbaseConnection.collection(connectionString);
        collection.replace(documentId, document);
    }
}
