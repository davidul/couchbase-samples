package davidul.basic;


import com.fasterxml.jackson.annotation.JsonProperty;

public class SampleType {
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
