package data;

import authentication.User;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private User user;
    private List<ProductItem> items;

    public ShoppingCart(User user) {
        this.user = user;
        this.items = new ArrayList<>();
    }

    public User getUser() {
        return user;
    }

    public List<ProductItem> getItems() {
        return items;
    }

    public void addItem(ProductItem item) {
        this.items.add(item);
    }

    public void removeItem(ProductItem item) {
        this.items.remove(item);
    }

    public void clearCart() {
        this.items.clear();
    }
}