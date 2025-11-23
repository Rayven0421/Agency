package governmentagencysystem;

public class Clerk extends GovernmentEmployee {

    public Clerk(String fullName, String employeeId) {
        super(fullName, employeeId);
    }

    @Override
    public void process(ServiceRequest request, String note) {
        request.updateStatus("REVIEWED");
        request.addNote("[Clerk] " + note);
        System.out.println("\n[Clerk] Request " + request.getTrackingCode() + " moved to REVIEWED.");
    }
}
