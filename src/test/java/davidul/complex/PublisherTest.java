package davidul.complex;

import com.google.gson.Gson;
import davidul.complex.kafka.Publisher;
import io.vavr.collection.List;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.TestSuite;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

@RunWith(VertxUnitRunner.class)
public class PublisherTest {
    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
    private Vertx vertx;


    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();
        vertx.deployVerticle(Publisher.class.getName(), context.asyncAssertSuccess());
    }

    @Test
    public void _1(TestContext testContext) throws Exception {
        final String bootstrapServers = kafka.getBootstrapServers();
        final Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        final AdminClient adminClient = AdminClient.create(properties);
        final NewTopic my_topic = new NewTopic("my-topic", 1, (short) 1);
        adminClient.createTopics(Collections.singletonList(my_topic));

        vertx.eventBus().send(Main.KAFKA_PUBLISHER,"1,2,3");

    }
}
