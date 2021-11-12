package davidul.online.basic;

import com.couchbase.client.core.cnc.EventBus;

/**
 * @author ulicny.david@gmail.com
 */
public class Events {

    public static void main(String[] args) {
        final EventBus eventBus = CouchbaseConnection.cluster().environment().eventBus();
        eventBus.subscribe(System.out::println);
    }
}
