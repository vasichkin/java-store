package services;

import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class Catalog {
    public static Product exampleProduct1 = new Product(2411, "Nail gun", 28, 2395);
    public static Product exampleProduct2 = new Product(231, "Shotgun", 18, 305);
    private ArrayList<Product> products = new ArrayList();

    public Catalog() {
        products.add(exampleProduct1);
        products.add(exampleProduct2);
    }

    public ArrayList<Product> listProducts() {
        return products;
    }

    public Product getProduct(Integer id) {
        return products.stream()
                .filter(prd -> prd.id.equals(id))
                .findAny()
                .orElse(null);
    }

    public boolean addProduct(Product product) {
        if (products.contains(product)) {
            return false;
        }
        products.add(product);
        return true;
    }

    public boolean updateProduct(Product product) {
        // TODO
        deleteProduct(product.id);
        addProduct(product);
        return true;
    }

    public boolean deleteProduct(Integer id) {
        products.remove(getProduct(id));
        return true;
    }
}
