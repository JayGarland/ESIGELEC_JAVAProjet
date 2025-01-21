package service;

import authentication.Driver;
import data.DatabaseManager;
import data.Delivery;
import data.Mission;
import utils.ReportGenerator;
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

    // Method to get all deliveries that have been assigned to a driver
    public List<Delivery> getScheduledDeliveries() {
        List<Delivery> allDeliveries = databaseManager.getAllDeliveries();
        if (allDeliveries != null) {
            return allDeliveries.stream()
                    .filter(delivery -> delivery.getDriverId() > 0)
                    .toList();
        }
        return null;
    }

    public boolean assignDriverToDelivery(int deliveryId, int driverId) {
        Delivery delivery = databaseManager.getDeliveryById(deliveryId);
        if (delivery != null) {
            delivery.setDriverId(driverId);
            return databaseManager.updateDelivery(delivery);
        }
        return false;
    }

    public boolean generateReport(Date date) {
        List<Delivery> deliveries = databaseManager.getAllDeliveries();
        if (deliveries != null) {
            return reportGenerator.generateWordReport(date, deliveries);
        }
        return false;
    }
}