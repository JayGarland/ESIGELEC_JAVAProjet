package authentication;

public class Driver extends User {
    private String truckRegNumber;
    private int truckCapacityKg;

    public Driver() {
        super();
    }

    public Driver(String email, String password, String phoneNumber, String truckRegNumber, int truckCapacityKg) {
        super(email, password, phoneNumber, UserRole.DRIVER);
        this.truckRegNumber = truckRegNumber;
        this.truckCapacityKg = truckCapacityKg;
    }

    public String getTruckRegNumber() {
        return truckRegNumber;
    }

    public void setTruckRegNumber(String truckRegNumber) {
        this.truckRegNumber = truckRegNumber;
    }

    public int getTruckCapacityKg() {
        return truckCapacityKg;
    }

    public void setTruckCapacityKg(int truckCapacityKg) {
        this.truckCapacityKg = truckCapacityKg;
    }
}
