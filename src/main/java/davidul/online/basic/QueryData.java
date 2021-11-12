package davidul.online.basic;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryResult;

import java.util.List;


/**
 * Create primary index first.
 * create primary index prim_idx on `default`;
 */
public class QueryData {

    public static List<JsonObject> query(String[] args) {
        final QueryResult query = SimpleCouchbaseConnection.cluster().query("select * from `default` d where META(d).id='ID::1'");
        return query.rowsAsObject();

    }
}
