package davidul.complex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.eventbus.EventBus;
import reactor.core.publisher.Flux;

/**
 * Generates monotonically increasing sequence of integers.
 *
 * @author ulicny.david@gmail.com
 */
public class MainCounterGenerator extends AbstractVerticle {
    public static final String ID = "ID::1";

    public static void main(String[] args) {
        Launcher.main(new String[]{"run", MainCounterGenerator.class.getName()});
    }

    @Override
    public void start() {
        generate();
    }

    public void generate(){
        System.out.println("Generating");
        final EventBus eventBus = vertx.eventBus();

        eventBus.consumer("generator", m -> {
            final Flux<String> ids = Flux.range(1, Main.IDS).map(i -> "ID::" + i);
            final Flux<String> range = Flux.range(1, 100).map(Object::toString);
            ids.flatMap(id -> range.map(r -> new Message(r,id, Main.MAIN_COUNTER)))
                    .subscribe(c -> {
                        System.out.println("Sending " + c.toString());
                        eventBus.send(Main.KAFKA_PUBLISHER, c.toString());
                    });
        });
    }
}
