package davidul.online.basic;

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
import davidul.online.basic.sampledata.SampleType;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.util.Optional;

import static davidul.online.basic.RetrieveData.retrieveIfExist;
import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveDataTest {

    private static String connectionString;
    private static final String ID_1 = "ID::1";

    @BeforeClass
    public static void setup() {
        connectionString = ContainerSetup.setup();
        upsert(ID_1);
    }

    @Test
    public void sample_retrieve() {
        final Optional<GetResult> res1 = retrieveIfExist(connectionString, ID_1);
        assertThat(res1.isPresent()).isTrue();

        final Optional<GetResult> res2 = retrieveIfExist(connectionString, ID_1);
        assertThat(res2.get().cas()).isEqualTo(res1.get().cas());
    }

    @Test
    public void retrieve_non_existing(){
        final Optional<GetResult> not_exist_id = retrieveIfExist(connectionString, "NOT_EXIST_ID");
        assertThat(not_exist_id).isEmpty();
    }

    @Test
    public void sample_retrieve_in_tx() {
        final Cluster cluster = SimpleCouchbaseConnection.cluster(connectionString);
        final Transactions transactions = Transactions.create(cluster);
        final TransactionResult run = transactions.run(ctx -> {
            final TransactionGetResult transactionGetResult = ctx.get(SimpleCouchbaseConnection.defaultCollection(connectionString), ID_1);
        });

    }

    @Test
    public void retrieve_reactive() {
        final GetResult getResult = SimpleCouchbaseConnection
                .defaultCollection(connectionString)
                .get(ID_1);

        final JsonTranscoder jsonTranscoder = JsonTranscoder.create(DefaultJsonSerializer.create());
        final Transcoder.EncodedValue encode = jsonTranscoder.encode(sampleData());
        final JsonObject jsonObject = JsonObject.fromJson(encode.encoded());
        final SampleType decode = jsonTranscoder.decode(SampleType.class, encode.encoded(), 0);

        StepVerifier
                .create(RetrieveData.retrieveReactive(connectionString, ID_1))
                .expectNext(jsonObject)
                .verifyComplete();
    }

    public static void upsert(String id){
        final Collection collection = SimpleCouchbaseConnection.defaultCollection(connectionString);
        collection.upsert(id, sampleData());
    }

    public static SampleType sampleData(){
        final SampleType sampleType = new SampleType();
        sampleType.setFirstName("First");
        sampleType.setLastName("Last");
        return sampleType;
    }

}
