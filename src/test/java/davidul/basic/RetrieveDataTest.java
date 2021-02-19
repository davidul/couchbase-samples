package davidul.basic;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.transactions.TransactionGetResult;
import com.couchbase.transactions.TransactionResult;
import com.couchbase.transactions.Transactions;
import davidul.ContainerSetup;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveDataTest {

    private static String connectionString;
    private static final String ID_1 = "ID::1";

    @BeforeClass
    public static void setup(){
        connectionString = ContainerSetup.setup();
    }

    @Test
    public void sample_retrieve(){
        final Upsert upsert = new Upsert();
        upsert.upsert(connectionString, ID_1 );
        final RetrieveData retrieveData = new RetrieveData();
        final GetResult res1 = retrieveData.retrieveIfExist(connectionString, "ID::1");
        assertThat(res1).isNotNull();

        final RetrieveData retrieveData1 = new RetrieveData();
        final GetResult res2 = retrieveData1.retrieveIfExist(connectionString, "ID::1");
        assertThat(res2.cas()).isEqualTo(res1.cas());
    }

    @Test
    public void sample_retrieve_in_tx(){
        final Upsert upsert = new Upsert();
        upsert.upsert(connectionString, ID_1);
        final Cluster cluster = CouchbaseConnection.cluster(connectionString);
        final Transactions transactions = Transactions.create(cluster);
        final TransactionResult run = transactions.run(ctx -> {
            final TransactionGetResult transactionGetResult = ctx.get(CouchbaseConnection.collection(connectionString), ID_1);
        });

    }

}
