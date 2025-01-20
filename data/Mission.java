package data;

import java.sql.Timestamp;

//mission is created and assign to driver by scheduler
public class Mission {
    private int id;
    private int driverId;
    private String route;
    private String status;
    private Timestamp timeCompleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Mission() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(Timestamp timeCompleted) {
        this.timeCompleted = timeCompleted;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        if (this.createdAt == null) {
            this.createdAt = createdAt;
        }
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        if (this.updatedAt == null) {
            this.updatedAt = updatedAt;
        }
    }
}
