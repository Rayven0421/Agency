package governmentagencysystem;

public class Citizen {
    private String fullName;
    private String tinNumber;
    private String idNumber;

    public Citizen(String fullName, String tinNumber, String idNumber) {
        this.fullName = fullName;
        this.tinNumber = tinNumber;
        this.idNumber = idNumber;
    }

    public String getFullName() { return fullName; }
    public String getTinNumber() { return tinNumber; }
    public String getIdNumber() { return idNumber; }

    @Override
    public String toString() {
        return fullName + " | ID: " + idNumber + " | TIN: " + tinNumber;
    }
}
