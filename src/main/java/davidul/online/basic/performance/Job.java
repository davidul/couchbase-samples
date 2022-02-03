package davidul.online.basic.performance;

import java.util.Date;

public class Job {
    private final String name;
    private final String status;
    private final String target;
    private final Date startDate;
    private final String customerId;

    public Job(String name, String status, String target, Date startDate, String customerId) {
        this.name = name;
        this.status = status;
        this.target = target;
        this.startDate = startDate;
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getTarget() {
        return target;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getCustomerId() {
        return customerId;
    }
}
