package davidul.online.connection;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.ReactiveCluster;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.codec.DefaultJsonSerializer;
import com.couchbase.client.java.codec.JsonTranscoder;

/**
 * Helper class for couchbase connection
 *
 * @author ulicny.david@gmail.com
 */
public class SimpleCouchbaseConnection {

    private static final String bucketName = "default";
    public static final String defaultUsername = "Administrator";
    public static final String defaultPassword = "password";

    private static Cluster cluster;
    private static ReactiveCluster reactiveCluster;

    /**
     * Collection with default bucket
     *
     * @return collection
     */
    public static Collection collection(){
        // Initialize the Connection
        cluster();
        Bucket bucket = cluster.bucket(bucketName);
        // get a collection reference
        return bucket.defaultCollection();
    }

    public static Collection collection(String connectionString, String collectionName){
        // Initialize the Connection
        cluster(connectionString);
        Bucket bucket = cluster.bucket(bucketName);
        // get a collection reference
        return bucket.collection(collectionName);
    }

    public static Collection defaultCollection(String connectionString){
        cluster(connectionString);
        Bucket bucket = cluster.bucket(bucketName);
        return bucket.defaultCollection();
    }

    /**
     * Local cluster, with default username and password
     *
     * @return local cluster
     */
    public static Cluster cluster(){
        // Initialize the Connection
        if(cluster == null)
            cluster = Cluster.connect("localhost", defaultUsername, defaultPassword);
        return cluster;
    }

    /**
     * Cluster with default user name and password
     * @param connectionString
     * @return cluster
     */
    public static Cluster cluster(String connectionString){
        if(cluster == null)
            cluster = Cluster.connect(connectionString, defaultUsername, defaultPassword);
        return cluster;
    }

    /**
     * Reactive collection
     * @param connectionString
     * @return reactive collection
     */
    public static ReactiveCollection reactiveCollection(String connectionString){
        reactiveCluster(connectionString);
        final ReactiveBucket bucket = reactiveCluster.bucket(bucketName);
        return bucket.defaultCollection();
    }

    public static ReactiveCluster reactiveCluster(String connectionString){
        if(reactiveCluster == null){
            reactiveCluster = ReactiveCluster.connect(connectionString, defaultUsername, defaultPassword);
        }
        return reactiveCluster;
    }

    public static JsonTranscoder defaultJsonTranscoder(){
        return JsonTranscoder.create(DefaultJsonSerializer.create());
    }
}
