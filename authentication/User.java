package authentication;

import java.sql.Timestamp;

public abstract class User {
    private int id;
    private String email;
    private String password;
    private String phoneNumber;
    private Timestamp createAt;
    private Timestamp updateAt;
    private UserRole role;

    public User() {
    }

    public User(String email, String password, String phoneNumber, UserRole role) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        if (this.createAt == null) {
            this.createAt = createAt;
        }
    }

    public Timestamp getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Timestamp updateAt) {
        if (this.updateAt == null) {
            this.updateAt = updateAt;
        }
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean login() {
        return false;
    };

    public boolean register() {
        return false;
    };

    public boolean update() {
        return false;
    };
}