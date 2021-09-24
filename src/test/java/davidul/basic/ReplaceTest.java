package davidul.basic;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.json.JsonObject;
import davidul.ContainerSetup;
import davidul.basic.sampledata.SampleData;
import org.junit.BeforeClass;
import org.junit.Test;

import static davidul.basic.Replace.replace;
import static davidul.basic.Upsert.upsert;
import static org.assertj.core.api.Assertions.assertThat;

public class ReplaceTest {

    private static String connectionString;
    private static final String ID_1 = "ID::1";
    private static final String ID_2 = "ID::2";

    @BeforeClass
    public static void setup() {
        connectionString = ContainerSetup.setup();
        upsert(connectionString, ID_1);
    }

    @Test(expected = DocumentNotFoundException.class)
    public void test_replace_not_exist() {
        replace(connectionString, ID_2, SampleData.sample());
    }

    @Test
    public void test_replace(){
        final JsonObject oldData = CouchbaseConnection
                .collection(connectionString)
                .get(ID_1)
                .contentAsObject();
        final JsonObject newData = SampleData
                .sample()
                .put("Update", "Value");

        replace(connectionString, ID_1, newData);

        final JsonObject replacedData = CouchbaseConnection
                .collection(connectionString)
                .get(ID_1)
                .contentAsObject();

        assertThat(replacedData.get("Update")).isEqualTo("Value");
    }
}