package davidul.versioning;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.transactions.TransactionGetResult;
import com.couchbase.transactions.Transactions;
import davidul.basic.CouchbaseConnection;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Optional;

public class Versioning {

    private static final String VERSION = "version";

    public void read(String key){
        final Collection collection = CouchbaseConnection.collection();
        final GetResult result = collection.get(key);
        final JsonObject jsonObject = result.contentAsObject();
        final String version = (String) jsonObject.get(VERSION);
    }

    public void swap(String key, JsonObject newVersion){
        final Cluster cluster = CouchbaseConnection.cluster();
        Transactions transactions = Transactions.create(cluster);

        transactions.run(ctx -> {
            final Collection collection = CouchbaseConnection.collection();

            final Optional<TransactionGetResult> transactionGetResult = ctx.getOptional(collection, key);

            final Optional<Tuple2<MutationResult, MutationResult>> objects = transactionGetResult.map(m -> {
                final JsonObject oldObject = m.contentAsObject();
                final Integer version = (Integer) oldObject.get(VERSION);
                final int value = version + 1;
                final JsonObject newObject = newVersion.put(VERSION, value);
                return Tuples.of(newObject, oldObject);
            }).map(f -> {
                final MutationResult upsert = collection.upsert(key, f.getT1());
                final MutationResult insert = collection.insert(key + "::v" + f.getT2().getInt(VERSION), f.getT2());
                return Tuples.of(upsert, insert);
            });

            ctx.commit();
        });

    }
}
