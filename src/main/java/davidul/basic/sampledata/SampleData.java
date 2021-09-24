package davidul.basic.sampledata;

import com.couchbase.client.java.json.JsonObject;

public class SampleData {

    public static final String KEY = "firstName";
    public static final String VALUE = "David";

    public static JsonObject sample(){
        //We will create very simple Json document
        return JsonObject
                .create()
                .put(KEY, VALUE);

    }
}
