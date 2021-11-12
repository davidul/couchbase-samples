package davidul.online.basic.mutation;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutationResult;
import davidul.online.basic.SimpleCouchbaseConnection;

public class Insert {
    public static MutationResult insert(String connectionString, String documentId, JsonObject object){
        return SimpleCouchbaseConnection.defaultCollection(connectionString).insert(documentId, object);
    }

    public static MutationResult insert(Collection collection, String documentId, JsonObject object){
        return collection.insert(documentId, object);
    }
}
