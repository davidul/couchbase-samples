package davidul.online;

import com.couchbase.client.core.env.IoConfig;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.env.ClusterEnvironment;
import com.couchbase.client.java.kv.MutationResult;
import davidul.online.basic.mutation.Insert;
import davidul.online.basic.sampledata.SampleData;
import org.junit.Test;

public class Scenario {

    @Test
    public void scenario(){
        ClusterEnvironment build = ClusterEnvironment.builder().ioConfig(IoConfig.enableDnsSrv(false)).build();
        ClusterOptions environment = ClusterOptions.clusterOptions("Administrator", "password").environment(build);
        Cluster cluster = Cluster.connect("127.0.0.1", environment);
        //Cluster cluster = CouchbaseConnection.cluster("localhost");
        //cluster.waitUntilReady(Duration.ofSeconds(20));
        Bucket bucket = cluster.bucket("default");
        //bucket.waitUntilReady(Duration.ofSeconds(20));
        Collection collection = bucket.defaultCollection();
        MutationResult insert = Insert.insert(collection, "ID::1", SampleData.sample());

    }
}
