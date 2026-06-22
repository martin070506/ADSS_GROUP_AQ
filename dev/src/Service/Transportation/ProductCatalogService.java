package Service.Transportation;

import DAO.ProductDAO;
import Domain.Transportation.Product;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCatalogService {
    private final List<Product> products;
    private int productCounter=0;
    private final ProductDAO productDAO;

    public ProductCatalogService(ProductDAO productDAO) {
        this.products = new ArrayList<>();
        this.productDAO=productDAO;
        try {
            this.productCounter=productDAO.getHighestProductID();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadAllProductsFromDB() {
        try {
            this.products.addAll(productDAO.loadAllProducts());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addProduct(String name, int weight) {
        int id = productCounter++;
        Product product = new Product(id,name, weight);
        products.add(product);
        try {
            productDAO.addProduct(product);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void removeProduct(Product product) {
        products.remove(product);
    }

    public String getProductDisplay(int productId) {
        for (Product product : products)
            if (product.id() == productId)
                return product.toString();

        throw new IllegalArgumentException("Product ID not found: " + productId);
    }

    public List<Integer> getProductsId() {
        List<Integer> productsId = new ArrayList<>();
        for (Product product : products)
            productsId.add(product.id());

        return productsId;
    }

    public Map<Product, Integer> mapIndicesToProducts(Map<Integer, Integer> selectedIndices) {
        Map<Product, Integer> productMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : selectedIndices.entrySet())
            productMap.put(products.get(entry.getKey()), entry.getValue());

        return productMap;
    }

    public int getWeightForProduct(int productId) {
        for (Product product : products)
            if (product.id() == productId)
                return product.weight();

        throw new IllegalArgumentException("Product ID not found: " + productId);
    }
}