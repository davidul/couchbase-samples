package davidul.online.complex.kafka;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Publisher extends AbstractVerticle {

    public static final String ADDRESS = Publisher.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(Publisher.class);
    private KafkaProducer<String, String> producer;

    @Override
    public void start() throws Exception {
        LOGGER.info("Registering Kafka");
        producer = KafkaProducer.create(Vertx.vertx(),
                Config.producerProperties(config().getString("bootstrap")),
                String.class,
                String.class);
        final EventBus eventBus = vertx.eventBus();

        eventBus.consumer(ADDRESS, message -> {
            LOGGER.info("read from event bus");
            final JsonObject body = (JsonObject) message.body();
            final String topic = message.headers().get("topic");
            send(body, topic);
        });

        eventBus.send("generator", "What's up?");
    }

    public void send(JsonObject message, String topic) {
        LOGGER.info("+++++++++++++++++++++++++++++++++++++++++++++++++ Sending message");
        final KafkaProducerRecord<String, String> record = KafkaProducerRecord
                .create(topic,
                        message.getString("documentId"),
                        message.encode());
        producer.send(record);
    }
}
