package davidul.basic;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryResult;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Create primary index first.
 * create primary index prim_idx on `default`;
 */
public class QueryData {
    public static void main(String[] args) {
        //final QueryResult query = CouchbaseConnection.cluster().query("select * from `default` d where META(d).id='ID::1'");
        final QueryResult query = CouchbaseConnection.cluster().query("select item from `default` where `key`.entityType = \"com.amdocs.digital.ms.i15n.api.couchbase.dto.TimedI15NItemDTO\"");
        final List<JsonObject> jsonObjects = query.rowsAsObject();
        System.out.println(jsonObjects.size());
        final HashMap<Integer, Integer> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.keySet();
        final List<Integer> collect = objectObjectHashMap.values().stream().filter(p -> p % 2 != 0).collect(Collectors.toList());
    }
}
