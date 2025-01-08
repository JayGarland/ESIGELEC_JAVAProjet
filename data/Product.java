package data;

import java.sql.Timestamp;

public class Product {
    private int id;
    private String name;
    private double weightKg;
    private double pricePerKg;
    private int categoryId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Product() {
    }

    public Product(String name, double weightKg, double pricePerKg, int categoryId) {
        this.name = name;
        this.weightKg = weightKg;
        this.pricePerKg = pricePerKg;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public double getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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