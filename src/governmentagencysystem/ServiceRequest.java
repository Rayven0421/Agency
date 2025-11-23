package governmentagencysystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequest {

    private static int requestCounter = 1;

    private String trackingCode;
    private Citizen citizen;
    private String requestType;
    private String priorityLevel;
    private String currentStatus;
    private LocalDate dueDate;
    private List<String> statusHistory = new ArrayList<>();
    private List<String> notes = new ArrayList<>();

    public ServiceRequest(Citizen citizen, String requestType, String priorityLevel, LocalDate dueDate) {
        this.citizen = citizen;
        this.requestType = requestType;
        this.priorityLevel = priorityLevel;
        this.dueDate = dueDate;

        this.currentStatus = "PENDING";
        this.trackingCode = "REQ-" + LocalDate.now().getYear() + "-" + String.format("%05d", requestCounter++);
        addStatusHistory("PENDING");
    }

    public String getTrackingCode() { return trackingCode; }
    public Citizen getCitizen() { return citizen; }
    public String getRequestType() { return requestType; }
    public String getPriorityLevel() { return priorityLevel; }
    public String getCurrentStatus() { return currentStatus; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isOverdue() { return LocalDate.now().isAfter(dueDate); }

    public List<String> getStatusHistory() { return statusHistory; }

    public void addStatusHistory(String status) {
        statusHistory.add(status + " (" + LocalDate.now() + ")");
    }

    public void updateStatus(String newStatus) {
        this.currentStatus = newStatus;
        addStatusHistory(newStatus);
    }

    public void addNote(String note) {
        notes.add(note);
    }

    @Override
    public String toString() {
        return "\nRequest: " + trackingCode +
                "\nCitizen: " + citizen.getFullName() +
                "\nType: " + requestType +
                "\nPriority: " + priorityLevel +
                "\nStatus: " + currentStatus +
                "\nDue: " + dueDate +
                (isOverdue() ? " (OVERDUE)" : "") +
                "\n-----------------------------";
    }
}
