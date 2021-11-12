package davidul.online.basic;

import com.couchbase.client.java.kv.GetResult;
import davidul.ContainerSetup;
import org.junit.BeforeClass;
import org.junit.Test;

import static davidul.online.connection.SimpleCouchbaseConnection.defaultCollection;

public class TransactionErrorTest {
    private static String connectionString;

    @BeforeClass
    public static void setup(){
        connectionString = ContainerSetup.setup();
    }

    @Test
    public void error(){
        final TransactionError transactionError = new TransactionError();
        transactionError.findKey("SOME", connectionString);
        final GetResult result = defaultCollection(connectionString).get("SOME::1");
        System.out.println(result);
    }

    @Test
    public void error_reactive(){
        final TransactionError transactionError = new TransactionError();
        transactionError.findKeyReactive("SOME", connectionString);
    }
}
