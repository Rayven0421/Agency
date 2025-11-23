package governmentagencysystem;

import java.time.LocalDate;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import org.json.*;

public class GovernmentAgencySystem {

    private List<Citizen> citizenList = new ArrayList<>();
    private List<ServiceRequest> requestList = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    private Clerk clerk = new Clerk("Clerk John", "C001");
    private Officer officer = new Officer("Officer Anna", "O001");

    private final String DATA_FILE = "agency_data.json";

    private int readSafeInt() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }

    private void showWelcomeBanner() {
        System.out.println("\n========================================");
        System.out.println("   GOVERNMENT AGENCY INFORMATION SYSTEM");
        System.out.println("========================================");
    }

    private void registerCitizen() {
        System.out.println("\n--- Register New Citizen ---");

        String name = null;
        while (name == null) {
            System.out.print("Enter Full Name: ");
            String input = scanner.nextLine().trim();
            if (input.matches("[A-Za-z\\s\\-]+")) name = input;
            else System.out.println("Invalid name. Only letters, spaces, and hyphens allowed.");
        }

        String tin = null;
        while (tin == null) {
            System.out.print("Enter TIN Number (digits only, 9-12 chars): ");
            String input = scanner.nextLine().trim();
            if (input.matches("\\d{9,12}")) tin = input;
            else System.out.println("Invalid TIN. Must be 9-12 digits.");
        }

        String id = null;
        while (id == null) {
            System.out.print("Enter ID Number (alphanumeric, 4-10 chars): ");
            String input = scanner.nextLine().trim();
            boolean exists = citizenList.stream().anyMatch(c -> c.getIdNumber().equalsIgnoreCase(input));
            if (!input.matches("[A-Za-z0-9]{4,10}")) System.out.println("Invalid ID. Must be 4-10 alphanumeric characters.");
            else if (exists) System.out.println("A citizen with this ID already exists.");
            else id = input;
        }

        citizenList.add(new Citizen(name, tin, id));
        saveData();
        System.out.println("\nCitizen registered successfully!");
    }

    private void viewCitizens() {
        System.out.println("\n--- Registered Citizens ---");
        if (citizenList.isEmpty()) { System.out.println("No citizens found."); return; }
        citizenList.forEach(System.out::println);
    }

    private void createServiceRequest() {
        System.out.println("\n--- Create Service Request ---");

        if (citizenList.isEmpty()) {
            System.out.println("No citizens available. Register one first.");
            return;
        }

        int index = -1;
        while (index == -1) {
            System.out.println("Select Citizen:");
            for (int i = 0; i < citizenList.size(); i++)
                System.out.println((i + 1) + ". " + citizenList.get(i));
            System.out.print("Enter number: ");
            int input = readSafeInt() - 1;
            if (input >= 0 && input < citizenList.size()) index = input;
            else System.out.println("Invalid selection. Try again.");
        }

        System.out.print("Enter Request Type: ");
        String type = scanner.nextLine().trim();

        String priority = null;
        while (priority == null) {
            System.out.println("\nSelect Priority:");
            System.out.println("1. LOW  2. NORMAL  3. HIGH  4. CRITICAL");
            System.out.print("Choose: ");
            switch (readSafeInt()) {
                case 1 -> priority = "LOW";
                case 2 -> priority = "NORMAL";
                case 3 -> priority = "HIGH";
                case 4 -> priority = "CRITICAL";
                default -> System.out.println("Invalid selection. Try again.");
            }
        }

        LocalDate dueDate = null;
        while (dueDate == null) {
            System.out.print("Enter Due Date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();
            try {
                LocalDate parsedDate = LocalDate.parse(dateInput);
                if (parsedDate.isBefore(LocalDate.now()))
                    System.out.println("Due date cannot be in the past. Enter today or a future date.");
                else dueDate = parsedDate;
            } catch (Exception e) {
                System.out.println("Invalid date format. Please enter YYYY-MM-DD.");
            }
        }

        ServiceRequest request = new ServiceRequest(citizenList.get(index), type, priority, dueDate);
        requestList.add(request);
        saveData();
        System.out.println("\nRequest Created! Tracking Code: " + request.getTrackingCode());
    }

    private void processRequests() {
        List<ServiceRequest> actionable = new ArrayList<>();
        for (ServiceRequest req : requestList)
            if (!req.isOverdue() && (req.getCurrentStatus().equals("PENDING") || req.getCurrentStatus().equals("REVIEWED")))
                actionable.add(req);

        if (actionable.isEmpty()) { System.out.println("\nNo actionable requests."); return; }

        System.out.println("\n--- Actionable Requests ---");
        for (int i = 0; i < actionable.size(); i++) {
            ServiceRequest r = actionable.get(i);
            System.out.println((i + 1) + ". " + r.getTrackingCode() + " | " + r.getRequestType() + " | Status: " + r.getCurrentStatus());
        }

        int selection = -1;
        while (selection == -1) {
            System.out.print("Select a request to process: ");
            int input = readSafeInt() - 1;
            if (input >= 0 && input < actionable.size()) selection = input;
            else System.out.println("Invalid selection. Try again.");
        }

        ServiceRequest reqToProcess = actionable.get(selection);

        System.out.println("Who will process this request? 1. Clerk  2. Officer");
        int processor = -1;
        while (processor == -1) {
            System.out.print("Choose: ");
            int input = readSafeInt();
            if (input == 1 || input == 2) processor = input;
            else System.out.println("Invalid choice. Try again.");
        }

        System.out.print("Enter note: ");
        String note = scanner.nextLine();

        if (processor == 1) clerk.process(reqToProcess, note);
        else officer.process(reqToProcess, note);

        saveData();
    }

    private void viewAllRequests() {
        System.out.println("\n=== ALL SERVICE REQUESTS ===");
        if (requestList.isEmpty()) { System.out.println("No requests found."); return; }

        for (ServiceRequest r : requestList) {
            System.out.println("-----------------------------");
            System.out.println("Tracking Code : " + r.getTrackingCode());
            System.out.println("Citizen Name  : " + r.getCitizen().getFullName());
            System.out.println("Type          : " + r.getRequestType());
            System.out.println("Status        : " + r.getCurrentStatus());
            System.out.println("Priority      : " + r.getPriorityLevel());
            System.out.println("Due Date      : " + r.getDueDate() + (r.isOverdue() ? " (OVERDUE)" : ""));
        }
        System.out.println("-----------------------------");
    }

    private void saveData() {
        try {
            JSONObject root = new JSONObject();
            JSONArray citizensArray = new JSONArray();
            for (Citizen c : citizenList) {
                JSONObject obj = new JSONObject();
                obj.put("fullName", c.getFullName());
                obj.put("tinNumber", c.getTinNumber());
                obj.put("idNumber", c.getIdNumber());
                citizensArray.put(obj);
            }
            root.put("citizens", citizensArray);

            JSONArray requestsArray = new JSONArray();
            for (ServiceRequest r : requestList) {
                JSONObject obj = new JSONObject();
                obj.put("trackingCode", r.getTrackingCode());
                obj.put("citizenId", r.getCitizen().getIdNumber());
                obj.put("requestType", r.getRequestType());
                obj.put("priorityLevel", r.getPriorityLevel());
                obj.put("currentStatus", r.getCurrentStatus());
                obj.put("dueDate", r.getDueDate().toString());
                obj.put("statusHistory", r.getStatusHistory());
                requestsArray.put(obj);
            }
            root.put("requests", requestsArray);

            try (FileWriter fw = new FileWriter(DATA_FILE)) { fw.write(root.toString(4)); }

        } catch (Exception e) { System.out.println("Error saving data: " + e.getMessage()); }
    }

    private void loadData() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return;
        try {
            String content = new String(Files.readAllBytes(f.toPath()));
            JSONObject root = new JSONObject(content);

            JSONArray citizensArray = root.getJSONArray("citizens");
            for (int i = 0; i < citizensArray.length(); i++) {
                JSONObject obj = citizensArray.getJSONObject(i);
                Citizen c = new Citizen(obj.getString("fullName"), obj.getString("tinNumber"), obj.getString("idNumber"));
                citizenList.add(c);
            }

            JSONArray requestsArray = root.getJSONArray("requests");
            for (int i = 0; i < requestsArray.length(); i++) {
                JSONObject obj = requestsArray.getJSONObject(i);
                Citizen c = citizenList.stream().filter(ci -> ci.getIdNumber().equals(obj.getString("citizenId"))).findFirst().orElse(null);
                if (c == null) continue;
                ServiceRequest r = new ServiceRequest(c, obj.getString("requestType"), obj.getString("priorityLevel"), LocalDate.parse(obj.getString("dueDate")));
                r.updateStatus(obj.getString("currentStatus"));
                JSONArray history = obj.getJSONArray("statusHistory");
                r.getStatusHistory().clear();
                for (int j = 0; j < history.length(); j++) r.getStatusHistory().add(history.getString(j));
                requestList.add(r);
            }

        } catch (Exception e) { System.out.println("Error loading data: " + e.getMessage()); }
    }

    public void startSystem() {
        loadData();
        while (true) {
            showWelcomeBanner();
            System.out.println("1. Register Citizen  2. View Citizens  3. Create Service Request");
            System.out.println("4. View All Requests  5. Process Requests  6. Exit");
            System.out.print("\nSelect an option: ");

            int choice = readSafeInt();
            switch (choice) {
                case 1 -> registerCitizen();
                case 2 -> viewCitizens();
                case 3 -> createServiceRequest();
                case 4 -> viewAllRequests();
                case 5 -> processRequests();
                case 6 -> {
                    saveData();
                    System.out.println("\nThank you for using the system. Goodbye!");
                    return;
                }
                default -> System.out.println("\nInvalid option. Please try again.");
            }
        }
    }

    public static void main(String[] args) {
        new GovernmentAgencySystem().startSystem();
    }
}
