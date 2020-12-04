package davidul.complex.kafka;

import davidul.complex.NumberOfCharProcessor;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Consumer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    @Override
    public void start() throws Exception {
      //  super.start();

        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "172.16.101.6:9092");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("group.id", "my_group");
        config.put("auto.offset.reset", "earliest");
        //config.put("enable.auto.commit", "false");

        // use consumer for interacting with Apache Kafka
        KafkaConsumer<String, String> consumer = KafkaConsumer.create(vertx, config);

        consumer.subscribe("my-topic", event -> {
            if(event.succeeded()){
                LOGGER.info("Subscribed to Kafka");
            }
        });

        final EventBus eventBus = vertx.eventBus();

        consumer.handler(consumerRecord -> {
            LOGGER.info("!!!!!!!!!!!! Consuming");
            final String value = consumerRecord.value();
            eventBus.publish(NumberOfCharProcessor.ADDRESS,  value);
        });

    }
}
