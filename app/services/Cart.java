package services;

import javax.inject.Singleton;
import java.util.HashMap;

public class Cart {
    private HashMap<Product, Integer> content = new HashMap<>();

    public HashMap<Product, Integer> listCart() {
        return content;
    }

    public void empty() {
        content.clear();
    }

    public boolean addProduct(Product product, Integer quantity) {
        if (product.stock < quantity) {
            return false;
        }
        if (content.keySet().contains(product)) {
            content.put(product, content.get(product).intValue() + quantity);
        } else {
            content.put(product, quantity);
        }
        return true;
    }

    public boolean removeProduct(Product product, Integer quantity) {
        if (content.keySet().contains(product)) {
            if (content.get(product).intValue() > quantity) {
                content.put(product, content.get(product).intValue() - quantity);
            } else if (content.get(product).intValue() == quantity) {
                content.remove(product);
            }
            product.stock(quantity);
            return true;
        }
        return false;
    }
}
