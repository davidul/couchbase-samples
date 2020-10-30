package davidul.basic;

import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutationResult;

/**
 * Demonstrates the difference between {@link Collection#upsert(String, Object)}
 * and {@link Collection#insert(String, Object)}.
 * While upsert simply updates the key, insert will throw an exception
 * when key already exists.
 *
 * @author ulicny.david@gmail.com
 */
public class UpsertVsInsert {
    public static void main(String[] args) {
        final Collection collection = CouchbaseConnection.collection();
        final JsonObject jsonObject = JsonObject
                .create()
                .put("firstName", "David");

        //cleanup
        final MutationResult remove = collection.remove("ID::1");
        collection.insert("ID::1", jsonObject);
        collection.upsert("ID::1", jsonObject);
        try {
            collection.insert("ID::1", jsonObject);
        }catch (DocumentExistsException e){
            System.out.println("Document already exists");
        }
    }
}
