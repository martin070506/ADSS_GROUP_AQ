import Domain.*;
import Service.CompanyManager;

import java.util.LinkedList;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        List<Location> locations = new LinkedList<>();
        List<Product> products = new LinkedList<>();
        List<Supplier> suppliers = new LinkedList<>();
        List<Truck> trucks = new LinkedList<>();
        List<Driver> drivers = new LinkedList<>();

        fillLists(locations, products, suppliers, trucks, drivers);

        CompanyManager companyManager;
    }

    public static void fillLists(List<Location> locations, List<Product> products,
                                 List<Supplier> suppliers, List<Truck> trucks, List<Driver> drivers) {

        locations.add(new Location("Tel Aviv", "03-1234567", "Alice"));
        locations.add(new Location("Holon", "03-7654321", "Bob"));
        locations.add(new Location("Haifa", "04-1112233", "Charlie"));
        locations.add(new Location("Netanya", "09-4445566", "David"));
        locations.add(new Location("Beer Sheva", "08-9998877", "Eve"));
        locations.add(new Location("Jerusalem", "02-5556677", "Frank"));
        locations.add(new Location("Ashdod", "08-2223344", "Grace"));
        locations.add(new Location("Petah Tikva", "03-9990011", "Henry"));
        locations.add(new Location("Ramat Gan", "03-4445556", "Isabella"));
        locations.add(new Location("Rehovot", "08-7778899", "Jack"));

        products.add(new Product("Apple", 150));
        products.add(new Product("Banana", 120));
        products.add(new Product("Orange", 200));
        products.add(new Product("Lemon", 80));
        products.add(new Product("Pineapple", 900));

        suppliers.add(new Supplier(locations.get(0),
                new LinkedList<>(List.of(
                        new ProductPair(products.get(0), 100)))));
        suppliers.add(new Supplier(locations.get(1),
                new LinkedList<>(List.of(
                        new ProductPair(products.get(1), 100)))));
        suppliers.add(new Supplier(locations.get(2),
                new LinkedList<>(List.of(
                        new ProductPair(products.get(2), 100)))));
        suppliers.add(new Supplier(locations.get(3),
                new LinkedList<>(List.of(
                        new ProductPair(products.get(3), 100)))));
        suppliers.add(new Supplier(locations.get(4),
                new LinkedList<>(List.of(
                        new ProductPair(products.get(4), 100)))));

        trucks.add(new Truck(101, "Isuzu Sumo", 3500, 7500, 2));
        trucks.add(new Truck(102, "DAF LF", 5000, 12000, 2));
        trucks.add(new Truck(103, "Volvo FH", 8000, 26000, 3));
        trucks.add(new Truck(104, "Mercedes Actros", 9500, 32000, 3));
        trucks.add(new Truck(105, "Scania R500", 10000, 40000, 3));

        drivers.add(new Driver("Alice", 1));
        drivers.add(new Driver("Bob", 2));
        drivers.add(new Driver("Charlie", 2));
        drivers.add(new Driver("David", 3));
        drivers.add(new Driver("Eve", 3));
    }
}