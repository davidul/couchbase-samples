package davidul;

import davidul.basic.CouchbaseConnection;
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
                .withCredentials(CouchbaseConnection.defaultUsername, CouchbaseConnection.defaultPassword)
                .withEnabledServices(CouchbaseService.KV, CouchbaseService.INDEX, CouchbaseService.QUERY);
        couchbaseContainer.start();
        return couchbaseContainer.getConnectionString();
    }

    public static String setup_1(){
        BucketDefinition aDefault = new BucketDefinition("default");
        CouchbaseContainer cb1 = new CouchbaseContainer(DockerImageName.parse("couchbase:7.0.2").asCompatibleSubstituteFor("couchbase/server"))
                .withBucket(aDefault)
                .withCredentials("Administrator", "password");
        CouchbaseContainer cb2 = new CouchbaseContainer(DockerImageName.parse("couchbase:7.0.2").asCompatibleSubstituteFor("couchbase/server"))
                .withBucket(aDefault)
                .withCredentials("Administrator", "password")
                ;
        //DockerImageName.parse();

        cb1.start();
        //cb2.start();
        return cb1.getConnectionString();//+ " " + cb2.getConnectionString();
    }

}
