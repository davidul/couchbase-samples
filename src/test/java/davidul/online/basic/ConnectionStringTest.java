package davidul.online.basic;

import com.couchbase.client.core.cnc.DefaultEventBus;
import com.couchbase.client.core.env.SeedNode;
import com.couchbase.client.core.util.ConnectionString;
import com.couchbase.client.core.util.ConnectionStringUtil;
import org.junit.Test;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ConnectionStringTest {

    @Test
    public void simple_host(){
        final List<ConnectionString.UnresolvedSocket> localhost = ConnectionString.create("localhost").hosts();

        assertThat(localhost.size()).isEqualTo(1);
        assertThat(localhost.get(0).hostname()).isEqualTo("localhost");
        assertThat(localhost.get(0).port()).isEqualTo(0);
        assertThat(localhost.get(0).portType()).isEqualTo(Optional.empty());

        final List<ConnectionString.UnresolvedSocket> hosts = ConnectionString.create("localhost:1234").hosts();

        assertThat(hosts.get(0).hostname()).isEqualTo("localhost");
        assertThat(hosts.get(0).port()).isEqualTo(1234);
        assertThat(hosts.get(0).portType()).isEqualTo(Optional.empty());

    }

    @Test
    public void port_types(){
        final List<ConnectionString.UnresolvedSocket> hosts = ConnectionString.create("srv.k8s.org:8888=KV,srv.k8s.org:8889=MANAGER").hosts();
        assertThat(hosts.size()).isEqualTo(2);
        assertThat(hosts.get(0).portType().orElse(null)).isEqualTo(ConnectionString.PortType.KV);
        assertThat(hosts.get(0).port()).isEqualTo(8888);

        assertThat(hosts.get(1).portType().orElse(null)).isEqualTo(ConnectionString.PortType.MANAGER);
    }

    @Test
    public void schemas(){
        final List<ConnectionString.UnresolvedSocket> hosts = ConnectionString.create("couchbase://srv.k8s.org:8888=KV,couchbase://srv.k8s.org:8889=MANAGER").hosts();
        assertThat(hosts.size()).isEqualTo(2);
        assertThat(hosts.get(0).portType().orElse(null)).isEqualTo(ConnectionString.PortType.KV);
        assertThat(hosts.get(0).port()).isEqualTo(8888);

        assertThat(hosts.get(1).portType().orElse(null)).isEqualTo(ConnectionString.PortType.MANAGER);
    }

    @Test
    public void seed_nodes(){
        final Set<SeedNode> seedNodes = ConnectionStringUtil.seedNodesFromConnectionString("couchbase://srv.k8s.org:8888=KV,couchbase://srv.k8s.org:8889=MANAGER", false, false,
                DefaultEventBus.create(Schedulers.single()));

        assertThat(seedNodes.size()).isEqualTo(1);
        assertThat(seedNodes.stream().map(SeedNode::address).collect(Collectors.toList())).containsExactly("srv.k8s.org");
    }
}
