package davidul.complex.document;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.Json;

import java.util.List;

/**
 * Couchbase document
 *
 * @author ulicny.david@gmail.com
 */
public class DocumentWrapper {

    private TrekMessage trekMessage;

    private Integer documentVersion;

    private List<Counter> counters;

    private final String documentId;

    private List<TrekMessage> history;

    private Integer charCount;

    private String maxCharacter;

    @JsonCreator
    public DocumentWrapper(@JsonProperty("trekMessage") TrekMessage trekMessage,
                           @JsonProperty("documentVersion") Integer documentVersion,
                           @JsonProperty("counters") List<Counter> counters,
                           @JsonProperty("documentId")  String documentId) {
        this.trekMessage = trekMessage;
        this.documentVersion = documentVersion;
        this.counters = counters;
        this.documentId = documentId;
    }

    public Integer getDocumentVersion() {
        return documentVersion;
    }

    public List<Counter> getCounters() {
        return counters;
    }

    public TrekMessage getTrekMessage() {
        return trekMessage;
    }

    public String getDocumentId() {
        return documentId;
    }

    public Integer getCharCount() {
        return charCount;
    }

    public void setCharCount(Integer charCount) {
        this.charCount = charCount;
    }

    public void addCounter(Counter counter){
        counters.add(counter);
    }

    public void setCounters(List<Counter> counters) {
        this.counters = counters;
    }

    public void setDocumentVersion(Integer documentVersion) {
        this.documentVersion = documentVersion;
    }

    public void setTrekMessage(TrekMessage trekMessage) {
        this.trekMessage = trekMessage;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DocumentWrapper{");
        sb.append("trekMessage=").append(trekMessage);
        sb.append(", documentVersion=").append(documentVersion);
        sb.append(", counters=").append(counters);
        sb.append(", documentId='").append(documentId).append('\'');
        sb.append(", history=").append(history);
        sb.append(", charCount=").append(charCount);
        sb.append(", maxCharacter='").append(maxCharacter).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String toJsonString(){
        return Json.encode(this);
    }
}
