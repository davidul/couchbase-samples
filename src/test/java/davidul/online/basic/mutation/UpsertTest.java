package davidul.online.basic.mutation;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutationResult;
import davidul.ContainerSetup;
import davidul.online.basic.RetrieveData;
import davidul.online.basic.sampledata.SampleData;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static davidul.online.basic.mutation.Upsert.reactiveUpsert;
import static davidul.online.basic.mutation.Upsert.upsert;
import static org.assertj.core.api.Assertions.assertThat;

public class UpsertTest {

    private static String connectionString;
    private static final String ID_1 = "ID::1";
    private static final String ID_2 = "ID::2";

    @BeforeClass
    public static void setup(){
        connectionString = ContainerSetup.setup();
    }

    @Test
    public void upsert_(){
        upsert(connectionString,  ID_1, SampleData.sample());
        final JsonObject retrieve = RetrieveData.retrieve(connectionString, ID_1);
        assertThat(retrieve.get(SampleData.KEY)).isEqualTo(SampleData.VALUE);
    }

    @Test
    public void reactive_upsert(){
        final Mono<MutationResult> mutationResultMono = reactiveUpsert(connectionString, ID_1, SampleData.sample());
        StepVerifier
                .create(mutationResultMono)
                .expectNextCount(1)
                .verifyComplete();
    }
}
