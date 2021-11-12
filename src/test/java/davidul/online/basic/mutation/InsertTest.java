package davidul.online.basic.mutation;

import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.java.kv.MutationResult;
import davidul.ContainerSetup;
import davidul.online.connection.SimpleCouchbaseConnection;
import davidul.online.basic.sampledata.SampleData;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import static davidul.online.basic.mutation.Upsert.upsert;

public class InsertTest {

    private static String connectionString;
    private static final String ID_1 = "ID::1";
    private static final String ID_2 = "ID::2";

    @BeforeClass
    public static void setup() {
        connectionString = ContainerSetup.setup();
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
}
