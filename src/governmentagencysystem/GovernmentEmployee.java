package governmentagencysystem;

public abstract class GovernmentEmployee {

    protected String fullName;
    protected String employeeId;

    public GovernmentEmployee(String fullName, String employeeId) {
        this.fullName = fullName;
        this.employeeId = employeeId;
    }

    public abstract void process(ServiceRequest request, String note);
}


