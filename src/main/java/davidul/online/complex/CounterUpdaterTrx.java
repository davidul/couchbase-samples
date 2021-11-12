package davidul.online.complex;

import com.couchbase.client.core.cnc.Event;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.ReplaceOptions;
import com.couchbase.client.java.kv.UnlockOptions;
import com.couchbase.transactions.TransactionDurabilityLevel;
import com.couchbase.transactions.TransactionResult;
import com.couchbase.transactions.Transactions;
import com.couchbase.transactions.config.TransactionConfigBuilder;
import davidul.online.basic.CouchbaseConnection;
import davidul.online.complex.document.Counter;
import davidul.online.complex.document.DocumentWrapper;
import davidul.online.complex.kafka.Publisher;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;


/**
 *
 * @author ulicny.david@gmail.com
 */
public class CounterUpdaterTrx extends AbstractVerticle {

    public static final String ADDRESS = CounterUpdaterTrx.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(CounterUpdaterTrx.class);

    final Transactions tx = Transactions.create(CouchbaseConnection.cluster(Main.CONNECTION_STRING), TransactionConfigBuilder.create()
            .durabilityLevel(TransactionDurabilityLevel.PERSIST_TO_MAJORITY)
            .logOnFailure(true, Event.Severity.WARN)
            .build());

    private EventBus eventBus;
    private JsonArray followers;
    private String leader;

    @Override
    public void start() {
        final String counter = config().getString("counter");
        followers = config().getJsonArray("followers");
        leader = config().getString("leader");
        eventBus = vertx.eventBus();
        eventBus.consumer(ADDRESS, message -> {
            LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++");
            final String counterName = message.headers().get("COUNTER");
            LOGGER.info("Updating " + counterName);
            final DocumentWrapper documentWrapper = Json.decodeValue((String) message.body(), DocumentWrapper.class);
            LOGGER.info(documentWrapper.toString());
            updateCounter(counterName, Main.CONNECTION_STRING, documentWrapper);
        });
    }

    public void updateCounter(final String counterName,
                              final String connectionString,
                              DocumentWrapper documentWrapper) {
        final Collection collection = CouchbaseConnection.collection(connectionString);

        final GetResult result = collection.get(documentWrapper.getDocumentId());
        if (result.cas() == -1) {
            LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++");
            LOGGER.info("Item is locked " + documentWrapper.getDocumentId());
            final DeliveryOptions deliveryOptions = new DeliveryOptions();
            deliveryOptions.addHeader("topic", "my-topic");
            LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            LOGGER.info("Retry");
            eventBus.send(Publisher.ADDRESS, io.vertx.core.json.JsonObject.mapFrom(documentWrapper), deliveryOptions);
            return;
        }

        try {
            final TransactionResult run = tx.run((ctx) -> {

                final GetResult getResult = collection.getAndLock(documentWrapper.getDocumentId(), Duration.ofMillis(100));

                final long lockedCas = getResult.cas();
                final DocumentWrapper existinDocument = getResult.contentAs(DocumentWrapper.class);
                LOGGER.info("After lock " + counterName + " " + documentWrapper.getDocumentId() + "::" + documentWrapper.getDocumentVersion());
                LOGGER.info("Existing version " + existinDocument.getDocumentId() + "::" + existinDocument.getDocumentVersion());

                if(existinDocument.getDocumentVersion() > documentWrapper.getDocumentVersion()){
                    Counter counter = getCounter(existinDocument.getCounters(), counterName);

                    Integer counterValue = counter.getCounterValue();
                    counterValue = counterValue + 1;
                    counter.setCounterValue(counterValue);
                    final List<Counter> counters = replaceCounter(existinDocument.getCounters(), counter);
                    existinDocument.setCounters(counters);
                    existinDocument.setCharCount(documentWrapper.getCharCount());
                    existinDocument.setTrekMessage(documentWrapper.getTrekMessage());
                    LOGGER.info("Replacing " + existinDocument.toString());
                    collection.replace(existinDocument.getDocumentId(), existinDocument, ReplaceOptions.replaceOptions().cas(lockedCas));
                }else {
                    LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++");
                    LOGGER.info("Unlocking");
                    collection.unlock(existinDocument.getDocumentId(), lockedCas, UnlockOptions.unlockOptions().timeout(Duration.ofMillis(100)));
                }

               // LOGGER.info("Replaced " + counterName + " with value " + counter.intValue());
                ctx.commit();
            });
            LOGGER.info("Transaction takes " + run.timeTaken().toMillis());
        } catch (Exception e) {
            LOGGER.info("Failed with " + counterName + " " + documentWrapper.getDocumentId());
            e.printStackTrace();
            final DeliveryOptions deliveryOptions = new DeliveryOptions();
            deliveryOptions.addHeader("topic", "my-topic");
            eventBus.send(Publisher.ADDRESS, JsonObject.mapFrom(documentWrapper), deliveryOptions);
        }
    }

    public Counter getCounter(List<Counter> counters, String counterName){
        for(Counter c : counters){
            if(c.getCounterName().equalsIgnoreCase(counterName)){
                LOGGER.info("Existing counter " + counterName);
                return c;
            }
        }
        LOGGER.info("Creating new counter " + counterName);
        return new Counter(counterName, 0);
    }

    public List<Counter> replaceCounter(List<Counter> counters, Counter counter){
        final boolean remove = counters.remove(counter);
        LOGGER.info("Removed counter " + counter.getCounterName() + " " +remove);
        counters.add(counter);
        LOGGER.info("Add counter " + counter.getCounterName());
        return counters;

    }
}