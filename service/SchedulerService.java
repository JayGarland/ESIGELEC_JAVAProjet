package service;

import authentication.Driver;
import authentication.User;
import data.DatabaseManager;
import data.Delivery;
import data.Mission;
import utils.ReportGenerator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SchedulerService {

    private final DatabaseManager databaseManager;
    private final ReportGenerator reportGenerator;

    public SchedulerService() {
        this.databaseManager = DatabaseManager.getInstance();
        this.reportGenerator = new ReportGenerator();
    }

    public List<Delivery> getAllDeliveries() {
        return databaseManager.getAllDeliveries();
    }

    public List<Driver> getAllDrivers() {
        return databaseManager.getAllDrivers();
    }

    // Method to get all deliveries that have not been assigned to a driver
    public List<Delivery> getPendingDeliveries() {
        List<Delivery> allDeliveries = databaseManager.getAllDeliveries();
        if (allDeliveries != null) {
            return allDeliveries.stream()
                    .filter(delivery -> delivery.getDriverId() == 0)
                    .toList();
        }
        return null;
    }

    // Method to get all deliveries that have been assigned to a driver and are not part of any mission yet
    public List<Delivery> getScheduledDeliveries() {
        List<Delivery> allDeliveries = databaseManager.getAllDeliveries();
        if (allDeliveries != null) {
            return allDeliveries.stream()
                    .filter(delivery -> delivery.getDriverId() > 0
                            && databaseManager.getMissionsByDeliveryId(delivery.getId()).isEmpty())
                    .toList();
        }
        return null;
    }

    public Delivery assignDriverToDelivery(int deliveryId, int driverId) {
        Delivery delivery = databaseManager.getDeliveryById(deliveryId);
        if (delivery != null) {
            delivery.setDriverId(driverId);
            databaseManager.updateDelivery(delivery);
            return delivery;
        }
        return null;
    }

    public boolean generateReport(Date date) {
        List<Delivery> deliveries = databaseManager.getAllDeliveries();
        if (deliveries != null) {
            return reportGenerator.generateWordReport(date, deliveries);
        }
        return false;
    }

    public Mission createMission(List<Delivery> deliveries, Driver driver) {
        if (deliveries == null || deliveries.isEmpty() || driver == null) {
            return null;
        }
        Mission mission = new Mission();
        mission.setDriver(driver);
        List<String> route = new ArrayList<>();
        route.add("Warehouse");
        for (Delivery delivery : deliveries) {
            route.add(delivery.getDeliveryAddress());
        }
        route.add("Warehouse");
        mission.setRoute(route);
        mission.setStatus("Assigned");

        boolean created = databaseManager.addMission(mission);
        if (created) {
            return mission;
        }
        return null;

    }

    public User getUserById(int id) {
        return databaseManager.getUserById(id);
    }

    public Delivery getDeliveryById(int id) {
        return databaseManager.getDeliveryById(id);
    }
}