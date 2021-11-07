package davidul.complex;

import com.couchbase.client.core.cnc.Event;
import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.ReplaceOptions;
import com.couchbase.client.java.kv.UnlockOptions;
import com.couchbase.transactions.TransactionDurabilityLevel;
import com.couchbase.transactions.Transactions;
import com.couchbase.transactions.config.TransactionConfigBuilder;
import com.couchbase.transactions.error.TransactionFailed;
import davidul.basic.CouchbaseConnection;
import davidul.complex.document.Counter;
import davidul.complex.document.DocumentWrapper;
import davidul.complex.kafka.Publisher;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DocumentUpdater extends AbstractVerticle {

    final Transactions tx = Transactions.create(CouchbaseConnection.cluster(Main.CONNECTION_STRING),
            TransactionConfigBuilder.create()
                    .durabilityLevel(TransactionDurabilityLevel.PERSIST_TO_MAJORITY)
                    .logOnFailure(true, Event.Severity.WARN)
                    .build());

    public static final String ADDRESS = DocumentUpdater.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentUpdater.class);
    private List<EventListener<DocumentWrapper>> registeredListeners;

    /**
     * Verticle start method
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        registeredListeners = new ArrayList<>();
        final EventBus eventBus = vertx.eventBus();
        eventBus.consumer(ADDRESS, message -> {
            final DocumentWrapper documentWrapper = ((JsonObject) message.body()).mapTo(DocumentWrapper.class);
            update(documentWrapper);
        });

        Flux.<DocumentWrapper>create(sink -> {
            registerListener(new EventListener<DocumentWrapper>() {
                @Override
                public void onData(DocumentWrapper s) {
                    LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++");
                    LOGGER.info("Add to delay queue");
                    sink.next(s);
                }

                @Override
                public void onComplete() {
                    sink.complete();
                }
            });
        })
                .delayElements(Duration.ofMillis(1000))
                .subscribe(c -> {
                    LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++");
                    LOGGER.info("DelayQueue " + c);
                    update(c);
                });


    }

    /**
     * Update document
     *
     * @param documentWrapper
     */
    public void update(DocumentWrapper documentWrapper){
        final Collection collection = CouchbaseConnection.collection(Main.CONNECTION_STRING);
        try {
            final GetResult getResult = collection.get(documentWrapper.getDocumentId());
            if (getResult.cas() == -1) {
                LOGGER.info("++++++++++++++++++++++++++++++++++++++++++++++");
                LOGGER.info("Document is Locked " + documentWrapper.getDocumentId());
                for(EventListener<DocumentWrapper> e : this.registeredListeners){
                    e.onData(documentWrapper);
                }
            }
        }catch (DocumentNotFoundException e){
            //Document does not exist, upsert it
            collection.upsert(documentWrapper.getDocumentId(), documentWrapper);
            LOGGER.info("++++++++++++++++++++++++++++++++++++++++++++++");
            LOGGER.info("Document upserted");
        }
        try {
            tx.run(ctx -> {
                LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                try {
                    final GetResult andLock = collection.getAndLock(documentWrapper.getDocumentId(), Duration.ofMillis(100));
                    final DocumentWrapper existingDocument = andLock.contentAs(DocumentWrapper.class);
                    final Integer existingDocumentDocumentVersion = existingDocument.getDocumentVersion();
                    final Integer documentVersion = documentWrapper.getDocumentVersion();
                    if (existingDocumentDocumentVersion < documentVersion) {
                        existingDocument.setDocumentVersion(documentVersion);
                        existingDocument.setTrekMessage(documentWrapper.getTrekMessage());
                        final Counter counter = getCounter(existingDocument.getCounters(), MainCounterGenerator.class.getName());
                        if(counter != null){
                            Integer counterValue = counter.getCounterValue();
                            counterValue = counterValue + 1;
                            final List<Counter> counters = replaceCounter(existingDocument.getCounters(), counter);
                        }
                        collection.replace(existingDocument.getDocumentId(), existingDocument, ReplaceOptions.replaceOptions().cas(andLock.cas()));
                    }else {
                        collection.unlock(existingDocument.getDocumentId(), andLock.cas(), UnlockOptions.unlockOptions().timeout(Duration.ofMillis(100)));
                    }
                }catch (DocumentNotFoundException e){
                    collection.upsert(documentWrapper.getDocumentId(), documentWrapper);
                    LOGGER.info("Document upserted");
                }
                LOGGER.info("Item replaced");
                ctx.commit();
            });
        }catch (TransactionFailed e) {
            LOGGER.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            e.printStackTrace();
        }

        final DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.addHeader("topic", "my-topic");
        LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        LOGGER.info("Processing");
        vertx.eventBus().send(Publisher.ADDRESS, JsonObject.mapFrom(documentWrapper), deliveryOptions);
    }

    public void registerListener(EventListener<DocumentWrapper> e){
        this.registeredListeners.add(e);
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

interface EventListener<T> {
    void onData(T t);
    void onComplete();
}


