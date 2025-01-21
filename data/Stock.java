package data;

import java.sql.Timestamp;

public class Stock {
    private int productId;
    private double weight;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Stock() {
    }

    public Stock(int productId, double weight) {
        this.productId = productId;
        this.weight = weight;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getweight() {
        return weight;
    }

    public void setweight(double weight) {
        this.weight = weight;
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