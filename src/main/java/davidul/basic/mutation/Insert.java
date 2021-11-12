package davidul.basic.mutation;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutationResult;
import davidul.basic.CouchbaseConnection;

public class Insert {
    public static MutationResult insert(String documentId, JsonObject object){
        return CouchbaseConnection.collection().insert(documentId, object);
    }
}
