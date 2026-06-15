package Domain.Transportation;

import java.util.Map;

public class ProductFile {
    private final int fileNumber;
    private final Map<Integer, Integer> products;



    public ProductFile(Map<Integer, Integer> products, int fileNumber){
        this.products = products;
        this.fileNumber = fileNumber;
    }

    public Map<Integer, Integer> getProducts() {
        return products;
    }
    public int getFileNumber() {
        return fileNumber;
    }

    public void addProduct(int productId, int amount) {
        int currentAmount = products.getOrDefault(productId, 0);
        products.put(productId, currentAmount + amount);
    }

    public void addProducts(Map<Integer, Integer> newProducts) {
        for (Map.Entry<Integer, Integer> entry : newProducts.entrySet())
            addProduct(entry.getKey(), entry.getValue());
    }

    public void removeProducts(Map<Integer, Integer> productsToRemove) {
        for (Map.Entry<Integer, Integer> entry : productsToRemove.entrySet())
            removeProduct(entry.getKey(), entry.getValue());
    }

    public void removeProduct(int productId, int amount) {
        int currentAmount = products.getOrDefault(productId, 0);
        if (currentAmount <= amount)
            products.remove(productId);
        else
            products.put(productId, currentAmount - amount);
    }
}
