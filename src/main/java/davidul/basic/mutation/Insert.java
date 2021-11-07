package davidul.basic.mutation;

import com.couchbase.client.java.json.JsonObject;
import davidul.basic.CouchbaseConnection;

public class Insert {
    public void insert(String documentId, JsonObject object){
        CouchbaseConnection.collection().insert(documentId, object);
    }
}
