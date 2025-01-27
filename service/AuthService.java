package service;

import authentication.User;
import data.DatabaseManager;

public class AuthService {

    private final DatabaseManager databaseManager;

    public AuthService() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public boolean register(User user) {
        if (databaseManager.getUserByEmail(user.getEmail()) == null) {
            return databaseManager.addUser(user);
        }
        return false;
    }

    public User login(String email, String password) {
        User user = databaseManager.getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean updateUser(User user) {
        return databaseManager.updateUser(user);
    }

    public User getUserById(int id) {
        return databaseManager.getUserById(id);
    }

}