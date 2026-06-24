package Service.Transportation;

import DAO.ProductDAO;
import DTO.ProductDTO;
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
        try {
            this.productCounter = productDAO.getHighestProductID() + 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadAllProductsFromDB() {
        try {
            products.clear(); // תוקן: מונע כפילויות במקרה של טעינה חוזרת

            // ה-DAO מחזיר DTOs, ה-Service ממיר ל-Domain
            List<ProductDTO> dtos = productDAO.loadAllProducts();
            for (ProductDTO dto : dtos) {
                products.add(new Product(dto.id(), dto.name(), dto.weight()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addProduct(String name, int weight) {
        int id = productCounter++;
        Product product = new Product(id, name, weight);
        products.add(product);
        try {
            // המרה ל-DTO ושליחה ל-DAO
            ProductDTO dto = new ProductDTO(id, name, weight);
            productDAO.addProduct(dto);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeProduct(Product product) {
        products.remove(product);
        try {
            // תוקן: מוחק את המוצר גם ממסד הנתונים
            productDAO.removeProduct(product.id());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getProductDisplay(int productId) {
        for (Product product : products) {
            if (product.id() == productId) {
                return product.toString();
            }
        }
        throw new IllegalArgumentException("Product ID not found: " + productId);
    }

    public List<Integer> getProductsId() {
        List<Integer> productsId = new ArrayList<>();
        for (Product product : products) {
            productsId.add(product.id());
        }
        return productsId;
    }

    public Map<Product, Integer> mapIndicesToProducts(Map<Integer, Integer> selectedIndices) {
        Map<Product, Integer> productMap = new HashMap<>();

        // תוקן: חיפוש המוצר לפי ID ולא לפי אינדקס הרשימה
        for (Map.Entry<Integer, Integer> entry : selectedIndices.entrySet()) {
            int targetId = entry.getKey();
            Product foundProduct = null;

            for (Product p : products) {
                if (p.id() == targetId) {
                    foundProduct = p;
                    break;
                }
            }

            if (foundProduct != null) {
                productMap.put(foundProduct, entry.getValue());
            } else {
                throw new IllegalArgumentException("Product ID not found: " + targetId);
            }
        }
        return productMap;
    }

    public int getWeightForProduct(int productId) {
        for (Product product : products) {
            if (product.id() == productId) {
                return product.weight();
            }
        }
        throw new IllegalArgumentException("Product ID not found: " + productId);
    }
}