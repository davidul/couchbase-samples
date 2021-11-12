package davidul.online.complex;

import com.github.javafaker.Faker;
import davidul.online.complex.document.DocumentWrapper;
import davidul.online.complex.document.TrekMessage;
import davidul.online.complex.kafka.Publisher;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Properties;

@RunWith(VertxUnitRunner.class)
public class PublisherTest {
    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
    private Vertx vertx;

    private class TestProducer extends MockProducer<String, String>{

    }


    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();
        final io.vertx.core.json.JsonObject bootstrap = new io.vertx.core.json.JsonObject()
                .put("bootstrap", kafka.getBootstrapServers())
                .put("topic", "my-topic");

        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setConfig(bootstrap);
        vertx.deployVerticle(Publisher.class.getName(), deploymentOptions, context.asyncAssertSuccess());
    }

    @Test
    public void _1(TestContext testContext) throws Exception {
        final String bootstrapServers = kafka.getBootstrapServers();
        final Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        final AdminClient adminClient = AdminClient.create(properties);
        final NewTopic my_topic = new NewTopic("my-topic", 1, (short) 1);
        adminClient.createTopics(Collections.singletonList(my_topic));

        final Faker faker = new Faker();
        final TrekMessage trekMessage = new TrekMessage("1",
                faker.starTrek().location(),
                faker.starTrek().specie(),
                faker.starTrek().character(),
                LocalDateTime.now());
        final DocumentWrapper documentWrapper = new DocumentWrapper(trekMessage, 1, Collections.emptyList(), "ID::1");

        vertx.eventBus().send(Main.KAFKA_PUBLISHER, JsonObject.mapFrom(documentWrapper));

    }
}
