package governmentagencysystem;

public class Officer extends GovernmentEmployee {

    public Officer(String fullName, String employeeId) {
        super(fullName, employeeId);
    }

    @Override
    public void process(ServiceRequest request, String note) {
        request.updateStatus("COMPLETED");
        request.addNote("[Officer] " + note);
        System.out.println("\n[Officer] Request " + request.getTrackingCode() + " COMPLETED.");
    }
}


