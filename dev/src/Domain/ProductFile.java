package Domain;

import java.util.HashMap;
import java.util.Map;

public class ProductFile {
    private final int fileNumber;
    private Map<Product, Integer> products;



    public ProductFile(Map<Product, Integer> products, int fileNumber){
        this.products = products;
        this.fileNumber = fileNumber;
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }
    public int getFileNumber() {
        return fileNumber;
    }

    public void addProduct(Product product, int amount) {
        products.put(product, products.getOrDefault(product, 0) + amount);
    }

    public void addProducts(Map<Product, Integer> newProducts) {
        products = Product.combineProducts(new HashMap<>(products), newProducts);
    }

    public void removeProducts(Map<Product, Integer> productsToRemove) {
        products = Product.reduceProducts(new HashMap<>(products), productsToRemove);
    }

    public void removeProduct(Product product, int quantityToRemove) {
        int currentAmount = products.getOrDefault(product, 0);
        if (currentAmount <= quantityToRemove) {
            products.remove(product);
        } else {
            products.put(product, currentAmount - quantityToRemove);
        }
    }
}
