package davidul.basic;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryResult;

import java.util.List;


/**
 * Create primary index first.
 * create primary index prim_idx on `default`;
 */
public class QueryData {
    public static void main(String[] args) {
        final QueryResult query = CouchbaseConnection.cluster().query("select * from `default` d where META(d).id='ID::1'");
        final List<JsonObject> jsonObjects = query.rowsAsObject();
        System.out.println(jsonObjects.size());
    }
}
