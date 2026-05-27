package Service;

import Domain.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductService {
    private final List<Product> products;

    public ProductService(List<Product> products) {
        this.products = products;
    }
    public ProductService() {
        this.products = new ArrayList<>();
    }
    public void addProduct(String name, int weight) {
        products.add(new Product(name, weight));
    }
    public void removeProduct(Product product) {
        products.remove(product);
    }

    public Product getProduct(String productName) {
        for (Product product : products)
            if (product.toString().equals(productName))
                return product;

        throw new IllegalArgumentException("Product not found");
    }

    public List<String> getProductsDisplay() {
        List<String> display = new ArrayList<>();
        for (Product product : products) {
            display.add(product.name() + " (" + product.weight() + " kg)");
        }
        return display;
    }

    public Map<Product, Integer> mapStringsToProducts(Map<String, Integer> stockIndexes) {
        Map<Product, Integer> productMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : stockIndexes.entrySet())
            productMap.put(products.get(Integer.parseInt(entry.getKey())), entry.getValue());

        return productMap;
    }
}
