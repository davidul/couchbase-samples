package davidul.complex;

import org.junit.Test;
import reactor.core.publisher.Flux;

public class MainCounterGeneratorTest {

    private static String connectionString;

    /*@BeforeClass
    public static void setup(){
        connectionString = ContainerSetup.setup();
    }*/
    
    @Test
    public void _1(){
        final Flux<String> ids = Flux.range(1, Main.IDS).map(i -> "ID::" + i);
        final Flux<String> range = Flux.range(1, 100).map(i -> i.toString());
        ids.flatMap(s -> range.map(r -> new Message(s,r, Main.MAIN_COUNTER))).subscribe(c -> System.out.println(c));
    }
}
