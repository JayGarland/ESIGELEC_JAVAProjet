package data;

import java.sql.Timestamp;

public class Product {
    private int id;
    private String name;
    private double weightKg;
    private int categoryId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Product() {
    }

    public Product(String name, double weightKg, int categoryId) {
        this.name = name;
        this.weightKg = weightKg;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getStock() {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        Stock stock = dbManager.getStockByProductId(this.id);
        if (stock != null) {
            return stock.getweight();
        }
        // throw an exception where stock is not found
        return 0;
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