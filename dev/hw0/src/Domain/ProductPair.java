package Domain;

public class ProductPair {

    public final Product product;
    private int amount;

    public ProductPair(Product product, int amount) {
        this.product = product;
        this.amount = amount;
    }

    public int getAmount() { return amount; }
    public void setAmount(int amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Amount can't be negative");

        this.amount = amount;
    }

    @Override
    public String toString() {
        // Assuming Product has a getName() method
        return product.name() + " (Quantity: " + amount + ")";
    }
}
