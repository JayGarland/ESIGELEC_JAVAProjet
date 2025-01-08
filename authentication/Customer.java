package authentication;

public class Customer extends User {
    public Customer() {
        super();
    }

    public Customer(String email, String password, String phoneNumber) {
        super(email, password, phoneNumber, UserRole.CUSTOMER);
    }
}
