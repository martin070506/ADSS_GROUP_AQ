package Domain;

public class Product {

    private String name;
    private int weight; // Kg

    public Product(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }
    public int getWeight() {
        return weight;
    }
}
