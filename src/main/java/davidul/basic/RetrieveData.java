package davidul.basic;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.client.java.kv.GetResult;

import java.util.Map;

public class RetrieveData {
    public static void main(String[] args) {
        final Collection collection = CouchbaseConnection.collection();
        final ExistsResult exists = collection.exists("ID::1");
        if (exists.exists()) {
            final GetResult getResult = collection.get("ID::1");
            System.out.println(getResult.contentAsObject());
        }

        final Map<String, SampleType> map = collection.map("ID::1", SampleType.class);
    }
}
