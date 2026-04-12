public enum Product {

    Apple(1),
    Banana(2),
    Pineapple(5),
    Carrot(10),
    Orange(20);

    private final int weight; // Kg

    Product(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
