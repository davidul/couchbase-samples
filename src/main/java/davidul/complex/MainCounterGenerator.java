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
        final EventBus eventBus = vertx.eventBus();
        Flux.range(1,100)
                .subscribe(integer -> eventBus.send(Main.MAIN_COUNTER, new Message(integer.toString(), ID).toString()));
    }
}
