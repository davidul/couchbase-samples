package davidul.basic;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutationResult;

public class Upsert {

    public void upsert(String connectionString, String id) {
        // get a collection reference
        Collection collection = CouchbaseConnection.collection(connectionString);

        //We will create very simple Json document
        final JsonObject put = JsonObject
                .create()
                .put("firstName", "David");
        final MutationResult upsert = collection.upsert(id, put);
        System.out.println("Mutation result:" );
        System.out.println("CAS: " + upsert.cas());
        System.out.println("Token: " + upsert.mutationToken());
    }
}
