package davidul.basic.mutation;

import davidul.ContainerSetup;
import davidul.basic.sampledata.SampleData;
import org.junit.BeforeClass;
import org.junit.Test;

import static davidul.basic.mutation.Upsert.upsert;

public class InsertTest {

    private static String connectionString;
    private static final String ID_1 = "ID::1";
    private static final String ID_2 = "ID::2";

    @BeforeClass
    public static void setup() {
        connectionString = ContainerSetup.setup_1();
        //connectionString = ContainerSetup.setup();
        upsert(connectionString, ID_1, SampleData.sample());
    }

    @Test
    public void insert(){
        Insert.insert(ID_2, SampleData.sample());
    }
}
