package davidul.complex;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import davidul.basic.CouchbaseConnection;
import davidul.complex.kafka.Consumer;
import davidul.complex.kafka.Publisher;
import io.vavr.collection.List;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;

import static io.vavr.collection.List.range;

/**
 * Configure and deploy the counting verticles.
 * There is a main counter, the leader. There are followers
 * who tries to catch up the main counter. The json structure looks like
 * <br/>
 * <code>
 *     {"c0_counter":100,"c1_counter":100,"c2_counter":100,"main_counter":100}
 * </code>
 *
 * @author ulicny.david@gmail.com
 */
public class Main extends AbstractVerticle {

    public static final String MAIN_COUNTER = "main_counter";

    public static final String KAFKA_PUBLISHER = "kafka-publisher";

    public static final int NUM_COUNTERS = 3;

    public static final int IDS = 100;

    private static List<String> counters;

    public static final String CONNECTION_STRING = "172.16.101.2";

    public static void main(String[] args) {
        Launcher.main(new String[]{"run", Main.class.getName()});
    }

    @Override
    public void start() {
        counters = range(0, NUM_COUNTERS)
                        .map(i -> "c" + i + "_counter");
        initCouchbase();
        final DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setWorker(true);
        vertx.deployVerticle(new Publisher(), deploymentOptions);
        vertx.deployVerticle(new Consumer(), deploymentOptions);
        deployMainCounter();
        deployFollowers();

        vertx.deployVerticle(new MainCounterGenerator());

    }

    private void deployMainCounter() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions();
        final JsonArray followers = new JsonArray();
        counters.forEach(followers::add);
        final io.vertx.core.json.JsonObject counter = new io.vertx.core.json.JsonObject()
                .put("counter", MAIN_COUNTER)
                .put("followers", followers);

        deploymentOptions.setConfig(counter);
        deploymentOptions.setWorker(true);

        System.out.println("Deploying verticles");
        vertx.deployVerticle(new CounterUpdaterTrx(), deploymentOptions);
    }

    private void deployFollowers() {
        counters.forEach(counter -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions();
            final io.vertx.core.json.JsonObject jsonObject = new io.vertx.core.json.JsonObject()
                    .put("counter", counter)
                    .put("leader", MAIN_COUNTER);
            deploymentOptions.setConfig(jsonObject);
            deploymentOptions.setWorker(true);
            vertx.deployVerticle(new CounterUpdaterTrx(), deploymentOptions);
        });
    }

    public void initCouchbase(){
        final Collection collection = CouchbaseConnection.collection(CONNECTION_STRING);
        final JsonObject jsonObject = JsonObject.create().put(MAIN_COUNTER, 0);
        counters.forEach(counter -> jsonObject.put(counter, 0));

        range(1, IDS)
                .forEach(id ->
                    collection.upsert("ID::" + id, jsonObject));

    }
    public static List<String> getCounters() {
        return counters;
    }


}
