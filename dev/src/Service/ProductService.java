package Service;

import Domain.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private List<Product> products;

    public ProductService(List<Product> products) {
        this.products = products;
    }
    public ProductService() {
        this.products = new ArrayList<>();
    }
    public List<Product> getProducts() {
        return products;
    }
    public Product getProductByIndex(int index){
        return products.get(index);
    }
    public void addProduct(Product product) {
        products.add(product);
    }
    public void removeProduct(Product product) {
        products.remove(product);
    }
}
