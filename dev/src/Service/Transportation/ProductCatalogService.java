package Service.Transportation;

import DAO.Transportation.ProductDAO;
import DTO.Transportation.ProductDTO;
import Domain.Transportation.Product;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCatalogService {
    private final List<Product> products;
    private int productCounter;
    private final ProductDAO productDAO;

    public ProductCatalogService(ProductDAO productDAO) {
        this.products = new ArrayList<>();
        this.productDAO = productDAO;
        this.productCounter = productDAO.getHighestProductID() + 1;
    }

    public void loadAllProductsFromDB() {
        products.clear(); // תוקן: מונע כפילויות במקרה של טעינה חוזרת

        List<ProductDTO> dtos = productDAO.loadAllProducts();
        for (ProductDTO dto : dtos)
            products.add(new Product(dto.id(), dto.name(), dto.weight()));
    }

    public void addProduct(String name, int weight) {
        int id = productCounter++;
        Product product = new Product(id, name, weight);
        products.add(product);

        ProductDTO dto = new ProductDTO(id, name, weight);
        productDAO.addProduct(dto);
    }

    public void removeProduct(Product product) {
        products.remove(product);
        productDAO.removeProduct(product.id());
    }

    public String getProductDisplay(int productId) {
        return getProduct(productId).toString();
    }

    public List<Integer> getProductsId() {
        List<Integer> productsId = new ArrayList<>();
        for (Product product : products)
            productsId.add(product.id());

        return productsId;
    }

    public int getWeightForProduct(int productId) {
        return getProduct(productId).weight();
    }

    public String getProductsDisplay(Map<Integer, Integer> truckProducts) {
        StringBuilder sb = new StringBuilder();
        if (truckProducts != null && !truckProducts.isEmpty()) {
            for (Map.Entry<Integer, Integer> entry : truckProducts.entrySet())
                sb.append("- ").append(getProduct(entry.getKey()).name()).append(": ").append(entry.getValue()).append(" units\n");

            return sb.toString();
        }

        return null;
    }

    private Product getProduct(int productId){
        for (Product product : products)
            if (product.id() == productId)
                return product;

        throw new IllegalArgumentException("Product ID not found: " + productId);
    }

    public String getProductName(int productId) {
        return getProduct(productId).name();
    }
}