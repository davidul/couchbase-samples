package davidul.online.basic.performance;

import junit.framework.TestCase;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HighLoadTest extends TestCase {

    public void testInsertData() {
        final HighLoad highLoad = new HighLoad();
        highLoad.insertData(1000_000);
        //1 904 462 = 1904 = 31 minut
        //highLoad.deleteData(1000_000);
    }

    public void test_delete() {

    }

    //1000 items = 33 secs
    public void test_update() {
        final HighLoad highLoad = new HighLoad();
        highLoad.updateData();
    }

    public void test_update_reactive() {
        final HighLoad highLoad = new HighLoad();

        highLoad.updateReactive().subscribe();


        System.out.println("");
    }
}