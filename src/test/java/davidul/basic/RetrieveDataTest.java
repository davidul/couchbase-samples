package davidul.basic;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.transactions.TransactionGetResult;
import com.couchbase.transactions.TransactionResult;
import com.couchbase.transactions.Transactions;
import davidul.ContainerSetup;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.couchbase.CouchbaseService;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveDataTest {

    private static String connectionString;

    @BeforeClass
    public static void setup(){
        connectionString = ContainerSetup.setup();
    }

    @Test
    public void sample_retrieve(){
        final Upsert upsert = new Upsert();
        upsert.upsert(connectionString);
        final RetrieveData retrieveData = new RetrieveData();
        final GetResult res1 = retrieveData.sampleRetrieve(connectionString);
        assertThat(res1).isNotNull();

        final RetrieveData retrieveData1 = new RetrieveData();
        final GetResult res2 = retrieveData1.sampleRetrieve(connectionString);
        assertThat(res2.cas()).isEqualTo(res1.cas());
    }

    @Test
    public void sample_retrieve_in_tx(){
        final Upsert upsert = new Upsert();
        upsert.upsert(connectionString);
        final Cluster cluster = CouchbaseConnection.cluster(connectionString);
        final Transactions transactions = Transactions.create(cluster);
        final TransactionResult run = transactions.run(ctx -> {
            final TransactionGetResult transactionGetResult = ctx.get(CouchbaseConnection.collection(connectionString), "ID::1");
        });

    }

}
