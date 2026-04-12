import java.util.LinkedList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.printf("Hello world!");

        List<Location> locations = new LinkedList<>();
        List<Supplier> suppliers = new LinkedList<>();

        locations.add(new Location("Tel Aviv", "03-1234567", "Alice"));
        locations.add(new Location("Holon", "03-7654321", "Bob"));
        locations.add(new Location("Haifa", "04-1112233", "Charlie"));
        locations.add(new Location("Netanya", "09-4445566", "David"));
        locations.add(new Location("Beer Sheva", "08-9998877", "Eve"));

        suppliers.add(new Supplier(locations.get(0),
                new LinkedList<>(List.of(
                        new ProductPair(Product.Apple, 100)))));
        suppliers.add(new Supplier(locations.get(1),
                new LinkedList<>(List.of(
                        new ProductPair(Product.Banana, 100)))));
        suppliers.add(new Supplier(locations.get(2),
                new LinkedList<>(List.of(
                        new ProductPair(Product.Pineapple, 100)))));
        suppliers.add(new Supplier(locations.get(3),
                new LinkedList<>(List.of(
                        new ProductPair(Product.Carrot, 100)))));
        suppliers.add(new Supplier(locations.get(4),
                new LinkedList<>(List.of(
                        new ProductPair(Product.Orange, 100)))));
    }
}