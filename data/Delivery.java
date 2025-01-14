package data;

import java.sql.Timestamp;
import java.util.Date;

public class Delivery {
    private int id;
    private int customerId;
    private int driverId;
    private Date deliveryDate;
    private String deliveryAddress;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Delivery() {

    }

    public Delivery(int customerId, Date deliveryDate, String deliveryAddress, int driverId) {
        this.customerId = customerId;
        this.deliveryDate = deliveryDate;
        this.deliveryAddress = deliveryAddress;
        this.driverId = driverId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
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