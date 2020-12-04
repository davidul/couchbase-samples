package davidul.complex.document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Sample message. Contains just counter value and document ID.
 *
 * @author ulicny.david@gmail.com
 */
public class TrekMessage {
    private final String documentId;
    private final String location;
    private final String specie;
    private final String character;
    private final LocalDateTime dateTime;

    @JsonCreator
    public TrekMessage(@JsonProperty("documentId") String documentId,
                       @JsonProperty("location") String location,
                       @JsonProperty("specie") String specie,
                       @JsonProperty("character") String character,
                       @JsonProperty("dateTime")
                           @JsonSerialize(using = LocalDateTimeSerializer.class)
                               @JsonDeserialize(using = LocalDateTimeDeserializer.class)
                                   LocalDateTime dateTime) {
        this.documentId = documentId;
        this.location = location;
        this.specie = specie;
        this.character = character;
        this.dateTime = dateTime;
    }

    public String getLocation() {
        return location;
    }

    public String getSpecie() {
        return specie;
    }

    public String getCharacter() {
        return character;
    }

    public String getDocumentId() {
        return documentId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrekMessage that = (TrekMessage) o;
        return Objects.equals(documentId, that.documentId) && Objects.equals(location, that.location) && Objects.equals(specie, that.specie) && Objects.equals(character, that.character) && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, location, specie, character, dateTime);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TrekMessage{");
        sb.append("documentId='").append(documentId).append('\'');
        sb.append(", location='").append(location).append('\'');
        sb.append(", specie='").append(specie).append('\'');
        sb.append(", character='").append(character).append('\'');
        sb.append(", dateTime=").append(dateTime);
        sb.append('}');
        return sb.toString();
    }
}
