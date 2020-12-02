package davidul.basic;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;

/**
 * @author ulicny.david@gmail.com
 */
public class CouchbaseConnection {

    private static final String bucketName = "default";
    private static final String username = "Administrator";
    private static final String password = "password";
    private static Cluster cluster;

    public static Collection collection(){
        // Initialize the Connection
        cluster();
        Bucket bucket = cluster.bucket(bucketName);
        // get a collection reference
        Collection collection = bucket.defaultCollection();
        return collection;
    }

    public static Collection collection(String connectionString){
        // Initialize the Connection
        cluster(connectionString);
        Bucket bucket = cluster.bucket(bucketName);
        // get a collection reference
        Collection collection = bucket.defaultCollection();
        return collection;
    }


    public static Cluster cluster(){
        // Initialize the Connection
        if(cluster == null)
            cluster = Cluster.connect("localhost", username, password);
        return cluster;
    }

    public static Cluster cluster(String connectionString){
        if(cluster == null)
            cluster = Cluster.connect(connectionString, username, password);
        return cluster;
    }

}
