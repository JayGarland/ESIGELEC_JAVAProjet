package authentication;

public class Customer extends User {

    private String address;

    public Customer(String email, String password, String phoneNumber) {
        super(email, password, phoneNumber, UserRole.CUSTOMER);
    }

    public Customer(String email, String password, String phoneNumber, String address) {
        super(email, password, phoneNumber, UserRole.CUSTOMER);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}