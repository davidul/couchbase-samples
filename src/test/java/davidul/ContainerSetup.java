package davidul;

import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.couchbase.CouchbaseService;
import org.testcontainers.utility.DockerImageName;

public class ContainerSetup {
    public static String setup(){
        final BucketDefinition aDefault = new BucketDefinition("default");
        final DockerImageName dockerImageName = DockerImageName.parse("couchbase").asCompatibleSubstituteFor("couchbase/server").withTag("latest");
        final CouchbaseContainer couchbaseContainer = new CouchbaseContainer(dockerImageName)
                .withBucket(aDefault)
                .withCredentials("Administrator", "password")
                .withEnabledServices(CouchbaseService.KV, CouchbaseService.INDEX, CouchbaseService.QUERY);
        couchbaseContainer.start();
        return couchbaseContainer.getConnectionString();
    }

}
