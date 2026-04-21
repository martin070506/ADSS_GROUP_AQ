package Domain;

public class ProductPair {

    public final Product product;
    private int amount;

    public ProductPair(Product product, int amount) {
        this.product = product;
        this.amount = amount;
    }

    public ProductPair(ProductPair pair) {
        this.product = pair.product;
        this.amount = pair.amount;
    }

    public int getAmount() { return amount; }
    public void setAmount(int amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Amount can't be negative");

        this.amount = amount;
    }

    public void reduceAmount(int amount) {
        setAmount(getAmount() - amount);
    }

    public int getWeight() { return product.weight() * amount; }

    @Override
    public String toString() {
        return product.name() + " (Quantity: " + amount + ")";
    }
}
