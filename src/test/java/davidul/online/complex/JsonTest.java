package davidul.online.complex;

import com.github.javafaker.Faker;
import davidul.online.complex.document.DocumentWrapper;
import davidul.online.complex.document.TrekMessage;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class JsonTest {

    @Test
    public void marshall(){
        final Faker faker = new Faker();
        final TrekMessage trekMessage = new TrekMessage("ID::1",
                faker.starTrek().location(),
                faker.starTrek().specie(),
                faker.starTrek().character(),
                LocalDateTime.now());
        final DocumentWrapper documentWrapper = new DocumentWrapper(trekMessage, 1, new ArrayList<>(), "1");
        final JsonObject entries = JsonObject.mapFrom(documentWrapper);
        entries.mapTo(DocumentWrapper.class);
        entries.getJsonObject("trekMessage");
        final String encode = Json.encode(documentWrapper);
        Json.decodeValue(entries.toString(), DocumentWrapper.class);
    }
}
