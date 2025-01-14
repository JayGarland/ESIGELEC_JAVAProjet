package service;

import data.*;
import authentication.User;
import java.util.Date;
import java.util.List;

public class CustomerService {
    private final DatabaseManager databaseManager;

    public CustomerService() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    public void createDelivery(User customer, List<ProductItem> items, Date deliveryDate, String deliveryAddress,
            int driverId) {
        Delivery delivery = new Delivery(customer.getId(), deliveryDate, deliveryAddress, driverId);
        boolean deliveryCreated = databaseManager.addDelivery(delivery, items);

        if (deliveryCreated) {
            System.out.println("Delivery created successfully for customer: " + customer.getEmail());
            updateStock(items); // Update the stock after delivery is created
        } else {
            System.err.println("Error creating delivery for customer: " + customer.getEmail());
        }
    }

    private void updateStock(List<ProductItem> items) {
        for (ProductItem item : items) {
            Stock stock = databaseManager.getStockByProductId(item.getProduct().getId());
            if (stock != null) {
                double newQuantity = stock.getQuantityKg() - item.getQuantity();
                if (newQuantity >= 0) {
                    stock.setQuantityKg(newQuantity);
                    boolean stockUpdated = databaseManager.updateStock(stock);
                    if (stockUpdated) {
                        System.out.println("Stock updated for product: " + item.getProduct().getName());
                    } else {
                        System.err.println("Error updating stock for product: " + item.getProduct().getName());
                    }
                } else {
                    System.err.println("Not enough stock for product: " + item.getProduct().getName() + " Available: "
                            + stock.getQuantityKg() + " Requested: " + item.getQuantity());
                }
            } else {
                System.err.println("Stock not found for product: " + item.getProduct().getName());
            }
        }
    }

    public List<Product> getAllProducts() {
        return databaseManager.getAllProducts();
    }

    public Product getProductById(int id) {
        return databaseManager.getProductById(id);
    }
}