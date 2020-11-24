package davidul.complex;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import davidul.basic.CouchbaseConnection;
import io.vavr.collection.List;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.json.JsonArray;

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

    public static final int NUM_COUNTERS = 3;

    private static List<String> counters;

    public static void main(String[] args) {
        Launcher.main(new String[]{"run", Main.class.getName()});
    }

    @Override
    public void start() {
        counters = List.range(0, NUM_COUNTERS)
                        .map(i -> "c" + i + "_counter");
        initCouchbase();
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

        vertx.deployVerticle(new CounterUpdaterTrx(), deploymentOptions);
    }

    private void deployFollowers() {
        counters.forEach(counter -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions();
            final io.vertx.core.json.JsonObject jsonObject = new io.vertx.core.json.JsonObject().put("counter", counter);
            deploymentOptions.setConfig(jsonObject);
            deploymentOptions.setWorker(true);
            vertx.deployVerticle(new CounterUpdaterTrx(), deploymentOptions);
        });
    }

    public void initCouchbase(){
        final Collection collection = CouchbaseConnection.collection();
        final JsonObject jsonObject = JsonObject.create().put(MAIN_COUNTER, 0);
        counters.forEach(counter -> jsonObject.put(counter, 0));

        collection.upsert(MainCounterGenerator.ID, jsonObject);
    }
    public static List<String> getCounters() {
        return counters;
    }


}
