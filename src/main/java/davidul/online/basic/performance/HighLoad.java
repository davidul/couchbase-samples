package davidul.online.basic.performance;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.ReactiveCluster;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.client.java.query.ReactiveQueryResult;
import davidul.online.connection.SimpleCouchbaseConnection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static davidul.online.connection.SimpleCouchbaseConnection.collection;
import static davidul.online.connection.SimpleCouchbaseConnection.reactiveCollection;

/**
 * @author David Ulicny
 */
public class HighLoad {

    /**
     * Creates n objects and stores them to list.
     * @param n amount of objects to be created
     * @return
     */
    public List<Job> jobList(int n) {
        List<Job> jobList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            final Job job = new Job(String.valueOf(i), "waiting", "sample", new Date(), String.valueOf(i));
            jobList.add(job);
        }

        return jobList;
    }

    /**
     * Synchronously insert <pre>amount</pre>
     * of jobs.
     * @param amount number of jobs
     */
    public void insertData(int amount) {
        final long l = System.currentTimeMillis();

        jobList(amount)
                .forEach(job -> collection()
                        .insert(job.getCustomerId(), job));
        System.out.println("Insert " + (System.currentTimeMillis() - l));
    }

    public void updateData() {
        final long l = System.currentTimeMillis();
        final QueryResult query = SimpleCouchbaseConnection
                .cluster()
                .query("select * from `default` where customerId < \"1111\"");
        final List<JsonObject> jsonObjects = query.rowsAsObject();
        jsonObjects.get(0).get("default");
        //final List<Job> jobs = query.rowsAs(Job.class);
        final Collection collection = collection();
        jsonObjects.forEach(job -> {
            final JsonObject aDefault = (JsonObject) job.get("default");

            final Job updated = new Job(aDefault.getString("name"), "updated", aDefault.getString("target"),
                    new Date(aDefault.getLong("startDate")), aDefault.getString("customerId"));
            collection.upsert(updated.getCustomerId(), updated);
        });

        System.out.println("Updated " + (System.currentTimeMillis() - l));
    }

    public Flux<MutationResult> updateReactive() {
        final long l = System.currentTimeMillis();
        final ReactiveCluster reactiveCluster = SimpleCouchbaseConnection.reactiveCluster();
        final Mono<ReactiveQueryResult> query = reactiveCluster.query("select * from `default` where customerId < \"1111\"");

        return query
                .flatMapMany(ReactiveQueryResult::rowsAsObject)
                .flatMap(job -> {
                    final JsonObject aDefault = (JsonObject) job.get("default");

                    final Job updated = new Job(aDefault.getString("name"), "updated-reactive", aDefault.getString("target"),
                            new Date(aDefault.getLong("startDate")), aDefault.getString("customerId"));

                    return Mono.just(updated);
                }).flatMap(job ->
                        reactiveCluster.bucket("default").defaultCollection().upsert(job.getCustomerId(), job)
                );//.subscribe();
        //System.out.println("");
    }

    /**
     * Insert data in reactive mode
     * @param n
     */
    public void insertDataReactive(int n) {
        Flux.fromIterable(jobList(n))
                .flatMap(job ->
                        reactiveCollection().insert(job.getCustomerId(), job)
                                .doOnError(throwable -> throwable.printStackTrace()))
                .blockLast();
    }


    /**
     * Delete data
     * @param amount
     */
    public void deleteData(int amount) {
        for (int i = 0; i < amount; i++) {
            collection().remove(String.valueOf(i));
        }
    }

    /**
     * Delete data reactive
     * @return
     */
    public Flux<MutationResult> deleteDataReactive() {
        final ReactiveCluster reactiveCluster = SimpleCouchbaseConnection.reactiveCluster();
        final ReactiveCollection reactiveCollection = reactiveCluster.bucket("default").defaultCollection();
        final Mono<ReactiveQueryResult> query = reactiveCluster.query("select * from `default`");
        return query
                .flatMapMany(ReactiveQueryResult::rowsAsObject)
                .flatMap(object ->
                        reactiveCollection.remove(((JsonObject)object.get("default")).getString("customerId"))
                );
    }


    public static void main(String[] args) {
        final HighLoad highLoad = new HighLoad();
        //highLoad.deleteData(1000_000);
        //highLoad.deleteDataReactive().blockLast();
        highLoad.insertDataReactive(1000);;
        /*final MutationResult mutationResult = highLoad.updateReactive().blockLast();
        System.out.println("mutations " + mutationResult);
        Executors.newScheduledThreadPool(1).schedule(()-> System.out.println("" + LocalDateTime.now()), 1000, TimeUnit.MILLISECONDS);*/
    }
}
