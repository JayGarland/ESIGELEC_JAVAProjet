package data;

public class ProductItem {
    private Product product;
    private double quantity;

    public ProductItem(Product product, double quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public double getQuantity() {
        return quantity;
    }
}