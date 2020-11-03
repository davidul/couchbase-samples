package davidul.basic;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.client.java.kv.GetResult;

public class RetrieveData {

    public GetResult sampleRetrieve(String connectionString) {
        final Collection collection = CouchbaseConnection.collection(connectionString);

        final ExistsResult exists = collection.exists("ID::1");
        if (exists.exists()) {
            return collection.get("ID::1");
        }
        return null;
    }
}
