package davidul.basic;

import com.couchbase.client.core.cnc.EventBus;

public class Events {

    public static void main(String[] args) {
        final EventBus eventBus = CouchbaseConnection.cluster().environment().eventBus();
        eventBus.subscribe(e -> System.out.println(e));
    }
}
