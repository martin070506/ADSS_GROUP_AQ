package Domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class ProductFacade {
    // Key: Product Name, Value: The unique Product instance
    private final Map<String, Product> masterCatalog = new HashMap<>();

    public void addProduct(String name, double weight) {
        if (!masterCatalog.containsKey(name)) {
            masterCatalog.put(name, new Product(name, weight));
        }
    }

    public Product getProduct(String name) {
        return masterCatalog.get(name);
    }

    public Collection<Product> getAllProducts() {
        return masterCatalog.values();
    }
}