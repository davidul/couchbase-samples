package davidul.online.complex;

import davidul.online.complex.document.Counter;
import davidul.online.complex.document.DocumentWrapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberOfCharProcessor extends AbstractVerticle {

    public static final String ADDRESS = NumberOfCharProcessor.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(NumberOfCharProcessor.class);

    @Override
    public void start() throws Exception {
        final EventBus eventBus = vertx.eventBus();
        eventBus.consumer(ADDRESS, h -> {
            final String body = (String)h.body();
            final DocumentWrapper documentWrapper = Json.decodeValue(body, DocumentWrapper.class);
            documentWrapper.setCharCount(documentWrapper.getTrekMessage().getCharacter().length());
            LOGGER.info("Document ID " + documentWrapper.getDocumentId());
            LOGGER.info("Update number of chars " + documentWrapper.toString());
            final DeliveryOptions deliveryOptions = new DeliveryOptions();
            deliveryOptions.addHeader("COUNTER", ADDRESS);
            eventBus.send(CounterUpdaterTrx.ADDRESS, Json.encode(documentWrapper), deliveryOptions);
        });
    }

    public Counter counter(){
        return new Counter(ADDRESS, 0);
    }
}
