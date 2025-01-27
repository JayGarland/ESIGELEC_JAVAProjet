package service;

import data.DatabaseManager;
import data.Delivery;
import data.Mission;
import data.ProductItem;

import java.sql.Timestamp;
import java.util.List;

public class DriverService {

    private final DatabaseManager databaseManager;

    public DriverService() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public List<Mission> getMissionsByDriverId(int driverId, String status) {
        return databaseManager.getMissionsByDriverId(driverId, status);
    }

    public Mission getMissionById(int driverId, int missionId) {
        return databaseManager.getMissionById(missionId);
    }

    public boolean markMissionAsCompleted(int driverId, int missionId) {
        Mission mission = getMissionById(driverId, missionId);
        if (mission != null) {
            mission.setStatus("Completed");
            mission.setTimeCompleted(new Timestamp(System.currentTimeMillis()));
            return databaseManager.updateMission(mission);
        }
        return false;
    }

    public Delivery getDeliveryByAddress(String address) {
        List<Delivery> deliveries = databaseManager.getAllDeliveries();
        if (deliveries != null) {
            return deliveries.stream().filter(delivery -> delivery.getDeliveryAddress().equals(address)).findFirst()
                    .orElse(null);
        }
        return null;
    }

    public List<ProductItem> getDeliveryItems(int deliveryId) {
        return databaseManager.getDeliveryItems(deliveryId);
    }

}