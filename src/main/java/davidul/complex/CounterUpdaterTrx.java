package davidul.complex;

import com.couchbase.client.core.cnc.Event;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.ReplaceOptions;
import com.couchbase.transactions.TransactionDurabilityLevel;
import com.couchbase.transactions.TransactionResult;
import com.couchbase.transactions.Transactions;
import com.couchbase.transactions.config.TransactionConfigBuilder;
import davidul.basic.CouchbaseConnection;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *
 * @author ulicny.david@gmail.com
 */
public class CounterUpdaterTrx extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(CounterUpdaterTrx.class);
    final Transactions tx = Transactions.create(CouchbaseConnection.cluster(), TransactionConfigBuilder.create()
            .durabilityLevel(TransactionDurabilityLevel.PERSIST_TO_MAJORITY)
            .logOnFailure(true, Event.Severity.WARN)
            .build());

    private EventBus eventBus;

    private JsonArray followers;

    @Override
    public void start() {
        final String counter = config().getString("counter");
        followers = config().getJsonArray("followers");
        eventBus = vertx.eventBus();
        eventBus.consumer(counter, message -> {
            final String body = (String) message.body();
            final String[] split = body.split(",");
            LOGGER.info("Received Message " + counter + " " + message.body());
            updateCounter(split[1], counter, split[0], "localhost");
        });
    }

    public void updateCounter(final String id, final String counterName, final String value, final String connectionString) {
        final Collection collection = CouchbaseConnection.collection(connectionString);
        final GetResult result = collection.get(id);
        if (result.cas() == -1) {
            LOGGER.info("Item is locked " + counterName + " " + id + " " + value);
            Flux.<Message>push(fluxSink -> {
                fluxSink.next(new Message(value, id));
            }).distinct(Message::hashCode)
                    .delayElements(Duration.ofSeconds(1))
                    .subscribe(c -> {
                        LOGGER.info("Sending retrials " + c.toString());
                        eventBus.send(counterName, c.toString());
                    });

            LOGGER.info("Retry");
            return;
        }

        try {
            final TransactionResult run = tx.run((ctx) -> {
                LOGGER.info("Before lock " + counterName + " " + id + " " + value);

                final GetResult getResult = collection.getAndLock(id, Duration.ofMillis(100));

                final long lockedCas = getResult.cas();
                LOGGER.info("After lock " + counterName + " " + id + " " + value);

                final JsonObject jsonObject = getResult.contentAsObject();
                LOGGER.info("BEFORE " + counterName + jsonObject.toString());

                final AtomicInteger counter = new AtomicInteger((Integer) jsonObject.get(counterName));
                if (Integer.parseInt(value) > counter.get())
                    counter.getAndSet(Integer.parseInt(value));
                final JsonObject put = jsonObject.put(counterName, counter.intValue());
                collection.replace(id, put, ReplaceOptions.replaceOptions().cas(lockedCas));
                LOGGER.info("Replaced " + counterName + " with value " + counter.intValue());
                ctx.commit();
            });
            LOGGER.info("Transaction takes " + run.timeTaken().toMillis());
        } catch (Exception e) {
            LOGGER.info("Failed with " + counterName + " " + id + " " + value);
            e.printStackTrace();
            eventBus.send(counterName, new Message(value, id).toString());
        }


        if (followers != null) {
            followers.stream().forEach(o -> eventBus.send((String) o, new Message(value, id).toString()));
        }
    }

}