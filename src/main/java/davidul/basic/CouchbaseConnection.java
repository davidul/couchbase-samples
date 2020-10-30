package davidul.basic;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;

public class CouchbaseConnection {

    private static final String bucketName = "default";
    private static final String username = "Administrator";
    private static final String password = "Administrator";
    private static Cluster cluster;

    public static Collection collection(){
        // Initialize the Connection
        cluster();
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

}
