package davidul.complex.document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Counter {
    private final String counterName;
    private Integer counterValue;

    @JsonCreator
    public Counter(@JsonProperty("counterName") String counterName, @JsonProperty("counterValue") Integer counterValue) {
        this.counterName = counterName;
        this.counterValue = counterValue;
    }


    public String getCounterName() {
        return counterName;
    }

    public Integer getCounterValue() {
        return counterValue;
    }

    public void setCounterValue(Integer counterValue) {
        this.counterValue = counterValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Counter counter = (Counter) o;
        return Objects.equals(counterName, counter.counterName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(counterName);
    }
}
