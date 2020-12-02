package davidul.complex.kafka;

import davidul.complex.Main;
import davidul.complex.Message;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Publisher extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(Publisher.class);
    private KafkaProducer<String, String> producer;

    @Override
    public void start() throws Exception {
        //super.start();//172.16.101.6
        LOGGER.info("Registering Kafka");
        producer = KafkaProducer.create(Vertx.vertx(), Config.producerProperties("172.16.101.6:9092"),String.class, String.class);
        final EventBus eventBus = vertx.eventBus();
        eventBus.consumer(Main.KAFKA_PUBLISHER, message -> {
            LOGGER.info("read from event bus");
            final String body = (String) message.body();
            final String[] split = body.split(",");
            send(new Message(split[1], split[0], split[2]));
        });

        eventBus.send("generator", "What's up?");
    }

    public void send(Message message){
        LOGGER.info("!!!!!!!!!!!! Sending message");
        final KafkaProducerRecord<String, String> record = KafkaProducerRecord.create("my-topic", message.getId(), message.toString());
        producer.send(record);
    }
}
