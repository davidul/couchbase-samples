package davidul.complex;

import java.util.Objects;

/**
 * Sample message. Contains just counter value and document ID.
 *
 * @author ulicny.david@gmail.com
 */
public class Message {
    private final String count;
    private final String id;

    public Message(String count, String id) {
        this.count = count;
        this.id = id;
    }

    public String getCount() {
        return count;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(count, message.count) &&
                Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, id);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(count).append(",");
        sb.append(id);
        return sb.toString();
    }
}
