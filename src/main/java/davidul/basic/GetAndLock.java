package davidul.basic;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.ReplaceOptions;
import com.couchbase.transactions.TransactionDurabilityLevel;
import com.couchbase.transactions.Transactions;
import com.couchbase.transactions.config.TransactionConfigBuilder;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class GetAndLock {

    public GetResult getAndLock(String connectionString, String id){
        final Collection collection = CouchbaseConnection.collection(connectionString);
        return collection.getAndLock(id, Duration.ofSeconds(20));
    }

    public Mono<GetResult> getAndLockReactive(String connectionString, String id){
        final ReactiveCollection reactive = CouchbaseConnection.collection(connectionString).reactive();
        return reactive.getAndLock(id, Duration.ofSeconds(20));
    }

    public void getAndLockInTrx(String connectionString, String id){
        final Collection collection = CouchbaseConnection.collection(connectionString);
        final Transactions transactions = Transactions.create(CouchbaseConnection.cluster(connectionString), TransactionConfigBuilder.create().durabilityLevel(TransactionDurabilityLevel.MAJORITY_AND_PERSIST_TO_ACTIVE));
        transactions.run(ctx -> {
            final GetResult andLock = collection.getAndLock(id, Duration.ofMillis(1000));
            final JsonObject jsonObject = andLock.contentAsObject();
            jsonObject.put("trx", "trx");
            collection.replace(id, jsonObject, ReplaceOptions.replaceOptions().cas(andLock.cas()));
            ctx.commit();
        });
    }
}
