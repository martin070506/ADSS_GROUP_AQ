package Service;

import Domain.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCatalogService {
    private final List<Product> products;

    public ProductCatalogService(List<Product> products) {
        this.products = products;
    }
    public ProductCatalogService() {
        this.products = new ArrayList<>();
    }
    public void addProduct(String name, int weight) {
        products.add(new Product(name, weight));
    }
    public void removeProduct(Product product) {
        products.remove(product);
    }

    // FIXED: Safely retrieves by catalog index
    public Product getProductByIndex(int index) {
        if (index >= 0 && index < products.size()) {
            return products.get(index);
        }
        throw new IllegalArgumentException("Product index out of bounds: " + index);
    }

    public List<String> getProductsDisplay() {
        List<String> display = new ArrayList<>();
        for (Product product : products) {
            display.add(product.toString());
        }
        return display;
    }

    // FIXED: Maps UI selection index positions to Product domain components
    public Map<Product, Integer> mapIndicesToProducts(Map<Integer, Integer> selectedIndices) {
        Map<Product, Integer> productMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : selectedIndices.entrySet()) {
            productMap.put(getProductByIndex(entry.getKey()), entry.getValue());
        }
        return productMap;
    }
}