package davidul.complex;

import davidul.complex.document.TrekMessage;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

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
        ids.flatMap(s -> range.map(r -> new TrekMessage("", s,r, Main.MAIN_COUNTER, LocalDateTime.now()))).subscribe(c -> System.out.println(c));
    }
}
