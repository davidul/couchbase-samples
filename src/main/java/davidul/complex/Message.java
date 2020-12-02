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
    private final String counter;

    public Message(String count, String id, String counter) {
        this.count = count;
        this.id = id;
        this.counter = counter;
    }

    public String getCount() {
        return count;
    }

    public String getId() {
        return id;
    }

    public String getCounter() {
        return counter;
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
        return Objects.hash(count, id, counter);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(count).append(",");
        sb.append(id).append(",");
        sb.append(counter);
        return sb.toString();
    }
}
