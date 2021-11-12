package davidul.online.basic;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.transactions.TransactionGetResult;
import com.couchbase.transactions.TransactionResult;
import com.couchbase.transactions.Transactions;
import com.couchbase.transactions.error.TransactionFailed;

import static com.couchbase.transactions.Transactions.create;
import static davidul.online.basic.CouchbaseConnection.cluster;

public class TransactionError {

    public void findKey(String key, String connectionString) {
        final Cluster cluster = cluster(connectionString);
        final Transactions transactions = create(cluster);
        try {
            transactions.run(ctx -> {
                ctx.insert(CouchbaseConnection.collection(connectionString), "SOME::1", JsonObject.create().put("some", "value"));
                final TransactionGetResult transactionGetResult = ctx.get(CouchbaseConnection.collection(connectionString), key);
                ctx.commit();
            });
        } catch (TransactionFailed transactionFailed) {
            System.err.println("Transaction failed");
            final TransactionResult result = transactionFailed.result();
            result.attempts();
        }
    }

    public void findKeyReactive(String key, String connectionString) {
        create(cluster(connectionString))
                .reactive()
                .run(ctx -> ctx.get(CouchbaseConnection.collection(connectionString).reactive(), key)
            .then(ctx.commit()))
                .doOnError(c -> System.err.println(c.getCause()));



    }
}

