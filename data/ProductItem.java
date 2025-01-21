package data;

public class ProductItem {
    private Product product;
    private double weight;

    public ProductItem(Product product, double weight) {
        this.product = product;
        this.weight = weight;
    }

    public Product getProduct() {
        return product;
    }

    public double getWeight() {
        return weight;
    }
}