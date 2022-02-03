package davidul.online.basic.mutation;

import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.java.kv.MutationResult;
import davidul.online.basic.sampledata.SampleData;
import davidul.online.connection.SimpleCouchbaseConnection;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static davidul.online.basic.mutation.Upsert.upsert;

public class InsertTest {

    private static String connectionString;
    private static final String ID_1 = "ID::1";
    private static final String ID_2 = "ID::2";
    private static final String ID_3 = "ID::3";

    @BeforeClass
    public static void setup() {
        connectionString = "localhost";
        //connectionString = ContainerSetup.setup();
        upsert(connectionString, ID_1, SampleData.sample());
    }

    @Test
    public void insert(){
        MutationResult insert = Insert.insert(connectionString, ID_2, SampleData.sample());
        long cas = insert.cas();
        Assertions.assertThat(SimpleCouchbaseConnection.collection().get(ID_2).cas()).isEqualTo(cas);
    }

    @Test(expected = DocumentExistsException.class)
    public void insert_failed(){
        MutationResult insert = Insert.insert(connectionString,ID_1, SampleData.sample());
    }

    @Test
    public void insert_reactive(){
        final Mono<MutationResult> mutationResultMono = Insert.insertReactive(connectionString, ID_3, SampleData.sample());

        StepVerifier
                .create(mutationResultMono)
                .expectNextCount(1)
                .verifyComplete();
    }
}
