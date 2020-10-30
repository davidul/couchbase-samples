package davidul.versioning;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.transactions.TransactionGetResult;
import com.couchbase.transactions.Transactions;
import davidul.basic.CouchbaseConnection;

public class Versioning {

    public void read(String key){
        final Collection collection = CouchbaseConnection.collection();
        final GetResult result = collection.get(key);
        final JsonObject jsonObject = result.contentAsObject();
        final String version = (String) jsonObject.get("version");
    }

    public void swap(String key, JsonObject newVersion){
        final Cluster cluster = CouchbaseConnection.cluster();
        Transactions transactions = Transactions.create(cluster);
        transactions.run(ctx -> {
            final Collection collection = CouchbaseConnection.collection();
            final TransactionGetResult transactionGetResult = ctx.get(collection, key);
            final JsonObject existing = transactionGetResult.contentAsObject();
            final Integer version = (Integer) existing.get("version");
            final int value = version + 1;
            final JsonObject name = newVersion.put("name", value);

            collection.upsert(key, name);//update existing
            collection.insert(key + "::" + version, existing);//insert new version
        });

    }
}
