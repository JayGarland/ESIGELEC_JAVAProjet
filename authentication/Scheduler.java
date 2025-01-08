package authentication;

public class Scheduler extends User {
    public Scheduler() {
        super();
    }

    public Scheduler(String email, String password, String phoneNumber) {
        super(email, password, phoneNumber, UserRole.SCHEDULER);
    }
}
