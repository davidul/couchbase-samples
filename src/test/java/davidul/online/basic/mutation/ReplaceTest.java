package davidul.online.basic.mutation;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.MutationResult;
import davidul.ContainerSetup;
import davidul.online.basic.SimpleCouchbaseConnection;
import davidul.online.basic.sampledata.SampleData;
import org.junit.BeforeClass;
import org.junit.Test;

import static davidul.online.basic.mutation.Replace.replace;
import static davidul.online.basic.mutation.Upsert.upsert;
import static org.assertj.core.api.Assertions.assertThat;

public class ReplaceTest {

    private static String connectionString;
    private static final String ID_1 = "ID::1";
    private static final String ID_2 = "ID::2";

    @BeforeClass
    public static void setup() {
        connectionString = ContainerSetup.setup();
        upsert(connectionString, ID_1, SampleData.sample());
    }

    @Test(expected = DocumentNotFoundException.class)
    public void test_replace_not_exist() {
        replace(connectionString, ID_2, SampleData.sample());
    }

    @Test
    public void test_replace(){
        final JsonObject oldData = SimpleCouchbaseConnection
                .defaultCollection(connectionString)
                .get(ID_1)
                .contentAsObject();

        final JsonObject newData = SampleData
                .sample()
                .put("Update", "Value");

        final MutationResult mutationResult = replace(connectionString, ID_1, newData);

        final JsonObject replacedData = SimpleCouchbaseConnection
                .defaultCollection(connectionString)
                .get(ID_1)
                .contentAsObject();

        assertThat(replacedData.get("Update")).isEqualTo("Value");
        assertThat(mutationResult).isNotNull();
    }
}