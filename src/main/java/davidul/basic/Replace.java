package davidul.basic;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;

public class Replace {

    public void replace(String connectionString, String id){
        final Collection collection = CouchbaseConnection.collection(connectionString);
        final JsonObject jsonObject = JsonObject
                .create()
                .put("firstName", "David");

        final MutationResult remove = collection.remove(id);

        collection.insert(id, jsonObject);
        final GetResult getResult = collection.get(id);
        final JsonObject jsonObject1 = getResult.contentAsObject();
        jsonObject1.put("lastName", "Ulicny");
        collection.replace(id, jsonObject1);

    }
}
