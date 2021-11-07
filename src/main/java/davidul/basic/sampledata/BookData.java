package davidul.basic.sampledata;

import com.couchbase.client.java.json.JsonObject;

public class BookData {

    public static JsonObject create(String id, String title){
        return JsonObject
                .create()
                .put("id", id)
                .put("title", title);
    }
}
