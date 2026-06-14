package Service.Transportation;

import Domain.Transportation.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCatalogService {
    private final List<Product> products;
    private int productCounter=0;

    public ProductCatalogService(List<Product> products) {
        this.products = products;
    }
    public ProductCatalogService() {
        this.products = new ArrayList<>();
    }
    public void addProduct(String name, int weight) {
        products.add(new Product(productCounter++,name, weight));
    }
    public void removeProduct(Product product) {
        products.remove(product);
    }

    // FIXED: Safely retrieves by catalog index
    public Product getProductById(int id) {
        for (Product product : products) {
            if(product.id() == id) return product;
        }
        throw new IllegalArgumentException("Product index out of bounds: " + id);
    }

    public List<String> getProductsDisplay() {
        List<String> display = new ArrayList<>();
        for (Product product : products) {
            display.add(product.toString());
        }
        return display;
    }

    public Map<Product, Integer> mapIndicesToProducts(Map<Integer, Integer> selectedIndices) {
        Map<Product, Integer> productMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : selectedIndices.entrySet())
            productMap.put(getProductById(entry.getKey()), entry.getValue());

        return productMap;
    }

    public int getIndexByName(String s) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).name().equals(s)) {
                return i;
            }
        }
        return -1;
    }
}