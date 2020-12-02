package davidul.complex.kafka;

import davidul.complex.Message;
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
        consumer.handler(h -> {
            System.out.println(h.key());
        });
        consumer.subscribe("my-topic", event -> {
            if(event.succeeded()){
                LOGGER.info("Subscribed to Kafka");
            }
        });

        final EventBus eventBus = vertx.eventBus();

        consumer.handler(h -> {
            System.out.println("!!!!!!!!!!!! Consuming");
            final String[] split = h.value().split(",");
            eventBus.publish(split[2], new Message(split[1], split[0], split[2]).toString());
        });

    }
}
