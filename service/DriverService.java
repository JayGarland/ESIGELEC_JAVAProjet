package service;

import data.DatabaseManager;
import data.Mission;
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

    public boolean markMissionAsCompleted(int driverId, int missionId) {
        Mission mission = getMissionById(driverId, missionId);
        if (mission != null) {
            mission.setStatus("Completed");
            mission.setTimeCompleted(new Timestamp(System.currentTimeMillis()));
            return databaseManager.updateMission(mission);
        }
        return false;
    }

    private Mission getMissionById(int driverId, int missionId) {
        List<Mission> assignedMissions = databaseManager.getMissionsByDriverId(driverId, "Assigned");
        List<Mission> completedMissions = databaseManager.getMissionsByDriverId(driverId, "Completed");
        if (assignedMissions != null) {
            for (Mission mission : assignedMissions) {
                if (mission.getId() == missionId) {
                    return mission;
                }
            }
        }
        if (completedMissions != null) {
            for (Mission mission : completedMissions) {
                if (mission.getId() == missionId) {
                    return mission;
                }
            }
        }
        return null;
    }
}