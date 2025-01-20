package test;

import authentication.AuthService;
import authentication.Driver;
import authentication.User;
import data.DatabaseManager;
import data.Mission;
import driver.DriverUI;
import utils.GuiUtil;

import java.sql.Timestamp;
import java.util.Arrays;
import javax.swing.SwingUtilities;

public class DriverTest {

    public static void main(String[] args) {
        // Initialize DatabaseManager and AuthService
        DatabaseManager dbManager = DatabaseManager.getInstance();
        AuthService authService = new AuthService();

        // Create a new driver user
        Driver driver = new Driver("testdriver@example.com", "password", "123-456-7890", "ABC-123", 7000);

        // Register the driver using AuthService
        if (authService.register(driver)) {
            System.out.println("Driver registered successfully.");

            // Retrieve the driver from the database
            User loggedInDriver = authService.login(driver.getEmail(), driver.getPassword());

            if (loggedInDriver instanceof Driver) {
                Driver loggedInDriverObject = (Driver) loggedInDriver;
                // Create a new mission for the driver
                Mission mission = new Mission();
                mission.setDriver(loggedInDriverObject);
                mission.setRoute(Arrays.asList("Warehouse", "Customer A", "Customer B", "Warehouse"));
                mission.setStatus("Assigned");
                mission.setTimeCompleted(null);

                if (dbManager.addMission(mission)) {
                    System.out.println("Mission created successfully for driver: " + loggedInDriverObject.getEmail());
                    // Open the DriverUI
                    SwingUtilities.invokeLater(() -> {
                        DriverUI driverUI = new DriverUI(loggedInDriverObject);
                    });

                } else {
                    System.err.println("Error creating mission for driver: " + loggedInDriverObject.getEmail());
                }

            } else {
                System.err.println("Error retrieving user after login.");
            }

        } else {
            System.err.println("Error registering driver.");
        }
    }
}