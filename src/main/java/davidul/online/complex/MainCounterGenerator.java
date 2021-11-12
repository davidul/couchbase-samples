package davidul.online.complex;

import com.couchbase.client.core.cnc.Event;
import com.couchbase.client.java.Collection;
import com.couchbase.transactions.TransactionDurabilityLevel;
import com.couchbase.transactions.Transactions;
import com.couchbase.transactions.config.TransactionConfigBuilder;
import com.github.javafaker.Faker;
import davidul.online.connection.SimpleCouchbaseConnection;
import davidul.online.complex.document.Counter;
import davidul.online.complex.document.DocumentWrapper;
import davidul.online.complex.document.TrekMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Generates monotonically increasing sequence of integers.
 *
 * @author ulicny.david@gmail.com
 */
public class MainCounterGenerator extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainCounterGenerator.class);


    final Transactions tx = Transactions.create(SimpleCouchbaseConnection.cluster(Main.CONNECTION_STRING),
            TransactionConfigBuilder.create()
                    .durabilityLevel(TransactionDurabilityLevel.PERSIST_TO_MAJORITY)
                    .logOnFailure(true, Event.Severity.WARN)
                    .build());

    public static void main(String[] args) {
        Launcher.main(new String[]{"run", MainCounterGenerator.class.getName()});
    }

    @Override
    public void start() {
        generate();
    }

    public void generate(){
        LOGGER.info("Generating");
        final EventBus eventBus = vertx.eventBus();

        final Faker faker = new Faker();


        eventBus.consumer("generator", m -> {
            final Flux<String> ids = Flux
                    .range(1, Main.IDS)
                    .map(i -> "ID::" + i)
                    .delayElements(Duration.ofMillis(100))
                    .publishOn(Schedulers.single());

            final Flux<String> range = Flux
                    .range(1, 10)
                    .map(Object::toString)
                    .publishOn(Schedulers.single());


            ids.map(documentId -> {
                final TrekMessage trekMessage = new TrekMessage("0",
                    faker.starTrek().location(),
                    faker.starTrek().specie(),
                    faker.starTrek().character(),
                    LocalDateTime.now());
                return new DocumentWrapper(trekMessage,
                        Integer.parseInt("0"),
                        new ArrayList<>(),
                        documentId);
            }).subscribe(c -> {
                LOGGER.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                LOGGER.info("Sending " + c.toString());
                lockAndUpdate(c);
                eventBus.send(DocumentUpdater.ADDRESS,  JsonObject.mapFrom(c));});

            ids.flatMap(documentId -> range.map(trekId -> {
                final TrekMessage trekMessage = new TrekMessage(trekId,
                        faker.starTrek().location(),
                        faker.starTrek().specie(),
                        faker.starTrek().character(),
                        LocalDateTime.now());
                final DocumentWrapper documentWrapper = new DocumentWrapper(trekMessage,
                        Integer.parseInt(trekId),
                        new ArrayList<>(),
                        documentId);
                return documentWrapper;
            })).delaySequence(Duration.ofSeconds(10))
                    .subscribe(c -> {
                        LOGGER.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        LOGGER.info("Sending " + c.toString());
                        eventBus.send(DocumentUpdater.ADDRESS,  JsonObject.mapFrom(c));
                    });
        });
    }

    public void lockAndUpdate(DocumentWrapper inputDocument){
        final Collection collection = SimpleCouchbaseConnection.defaultCollection(Main.CONNECTION_STRING);
            inputDocument.addCounter(new Counter(this.getClass().getName(), 0));
            tx.reactive().run(ctx -> {
                        return ctx
                                .insert(collection.reactive(), inputDocument.getDocumentId(), inputDocument)
                                .then(ctx.commit());
            }).doOnError(c -> c.printStackTrace());
        }
}
