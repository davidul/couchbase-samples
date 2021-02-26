package davidul.basic;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.codec.DefaultJsonSerializer;
import com.couchbase.client.java.codec.JsonTranscoder;
import com.couchbase.client.java.codec.Transcoder;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.transactions.TransactionGetResult;
import com.couchbase.transactions.TransactionResult;
import com.couchbase.transactions.Transactions;
import davidul.ContainerSetup;
import davidul.basic.sampledata.SampleType;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveDataTest {

    private static String connectionString;
    private static final String ID_1 = "ID::1";

    @BeforeClass
    public static void setup() {
        connectionString = ContainerSetup.setup();
    }

    @Test
    public void sample_retrieve() {
        final Upsert upsert = new Upsert();
        upsert.upsert(connectionString, ID_1);
        final RetrieveData retrieveData = new RetrieveData();
        final GetResult res1 = retrieveData.retrieveIfExist(connectionString, "ID::1");
        assertThat(res1).isNotNull();

        final RetrieveData retrieveData1 = new RetrieveData();
        final GetResult res2 = retrieveData1.retrieveIfExist(connectionString, "ID::1");
        assertThat(res2.cas()).isEqualTo(res1.cas());
    }

    @Test
    public void sample_retrieve_in_tx() {
        final Upsert upsert = new Upsert();
        upsert.upsert(connectionString, ID_1);
        final Cluster cluster = CouchbaseConnection.cluster(connectionString);
        final Transactions transactions = Transactions.create(cluster);
        final TransactionResult run = transactions.run(ctx -> {
            final TransactionGetResult transactionGetResult = ctx.get(CouchbaseConnection.collection(connectionString), ID_1);
        });

    }

    @Test
    public void retrieve_reactive() {
        upsert(ID_1);
        final GetResult getResult = CouchbaseConnection.collection(connectionString).get(ID_1);
        System.out.println(getResult.contentAs(SampleType.class));
        final JsonTranscoder jsonTranscoder = JsonTranscoder.create(DefaultJsonSerializer.create());
        final Transcoder.EncodedValue encode = jsonTranscoder.encode(sampleData());
        final JsonObject jsonObject = JsonObject.fromJson(encode.encoded());
        final SampleType decode = jsonTranscoder.decode(SampleType.class, encode.encoded(), 0);

        final RetrieveData retrieveData = new RetrieveData();

        StepVerifier
                .create(retrieveData.retrieveReactive(connectionString, ID_1))
                .expectNext(jsonObject)
                .verifyComplete();
    }

    public static void upsert(String id){
        final Collection collection = CouchbaseConnection.collection(connectionString);
        collection.upsert(id, sampleData());
    }

    public static SampleType sampleData(){
        final SampleType sampleType = new SampleType();
        sampleType.setFirstName("First");
        sampleType.setLastName("Last");
        return sampleType;
    }

}
