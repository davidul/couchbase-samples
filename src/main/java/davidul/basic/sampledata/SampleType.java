package davidul.basic.sampledata;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SampleType implements Serializable {
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
