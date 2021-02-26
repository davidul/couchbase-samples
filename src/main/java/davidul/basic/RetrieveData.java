package davidul.basic;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.client.java.kv.GetResult;
import reactor.core.publisher.Mono;

/**
 * @author ulicny.david@gmail.com
 */
public class RetrieveData {

    public GetResult retrieveIfExist(String connectionString, String id) {
        final Collection collection = CouchbaseConnection.collection(connectionString);

        final ExistsResult exists = collection.exists(id);
        if (exists.exists()) {
            return collection.get(id);
        }
        return null;
    }

    public JsonObject retrieve(String connectionString, String id){
        final Collection collection = CouchbaseConnection.collection(connectionString);
        return collection.get(id).contentAsObject();
    }

    public Mono<JsonObject> retrieveReactive(String connectionString, String id){
        final ReactiveCollection reactive = CouchbaseConnection.collection(connectionString).reactive();
        return reactive.get(id).map(GetResult::contentAsObject);//.map(r -> Result.of(r, SampleType.class));
    }
}
