import Domain.*;
import Presentation.MainConsole;
import Service.BranchManager;
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

        CompanyManager companyManager = CompanyManager.getInstance(trucks, drivers, suppliers, locations);

        BranchManager branchManager1 = new BranchManager(locations.get(0));
        List<ProductPair> pairs1 = new LinkedList<>();
        pairs1.add(new ProductPair(products.get(5), 4));
        branchManager1.requestShipment(pairs1);

        BranchManager branchManager2 = new BranchManager(locations.get(1));
        List<ProductPair> pairs2 = new LinkedList<>();
        pairs2.add(new ProductPair(products.get(0), 6));
        branchManager2.requestShipment(pairs2);

        BranchManager branchManager3 = new BranchManager(locations.get(2));
        List<ProductPair> pairs3 = new LinkedList<>();
        pairs3.add(new ProductPair(products.get(7), 10));
        pairs3.add(new ProductPair(products.get(8), 5));
        branchManager3.requestShipment(pairs3);


        MainConsole console = new MainConsole(companyManager, trucks, drivers, locations, suppliers);
        console.run();
    }

    public static void fillLists(List<Location> locations, List<Product> products,
                                 List<Supplier> suppliers, List<Truck> trucks, List<Driver> drivers) {

        locations.add(new Location("Tel Aviv", "03-1234567", "Alice"));     // 0
        locations.add(new Location("Holon", "03-7654321", "Bob"));          // 1
        locations.add(new Location("Haifa", "04-1112233", "Charlie"));      // 2
        locations.add(new Location("Netanya", "09-4445566", "David"));      // 3
        locations.add(new Location("Beer Sheva", "08-9998877", "Eve"));     // 4
        locations.add(new Location("Jerusalem", "02-5556677", "Frank"));    // 5
        locations.add(new Location("Ashdod", "08-2223344", "Grace"));       // 6
        locations.add(new Location("Petah Tikva", "03-9990011", "Henry"));  // 7
        locations.add(new Location("Ramat Gan", "03-4445556", "Isabella")); // 8
        locations.add(new Location("Rehovot", "08-7778899", "Jack"));       // 9

        products.add(new Product("Apple", 150));      // 0
        products.add(new Product("Banana", 120));     // 1
        products.add(new Product("Orange", 200));     // 2
        products.add(new Product("Lemon", 80));       // 3
        products.add(new Product("Pineapple", 900));  // 4
        products.add(new Product("Kiwi", 250));       // 5
        products.add(new Product("Tomato", 10));      // 6
        products.add(new Product("Cucumber", 8));     // 7
        products.add(new Product("Milk", 6));         // 8
        products.add(new Product("Bread", 12));       // 9

        suppliers.add(new Supplier(locations.get(0), new LinkedList<>(List.of(new ProductPair(products.get(0), 100)))));
        suppliers.add(new Supplier(locations.get(1), new LinkedList<>(List.of(new ProductPair(products.get(1), 100)))));
        suppliers.add(new Supplier(locations.get(2), new LinkedList<>(List.of(new ProductPair(products.get(2), 100)))));
        suppliers.add(new Supplier(locations.get(3), new LinkedList<>(List.of(new ProductPair(products.get(3), 100)))));
        suppliers.add(new Supplier(locations.get(4), new LinkedList<>(List.of(new ProductPair(products.get(4), 100)))));
        suppliers.add(new Supplier(locations.get(5), new LinkedList<>(List.of(new ProductPair(products.get(5), 100)))));

        suppliers.add(new Supplier(locations.get(6), new LinkedList<>(List.of(
                new ProductPair(products.get(6), 80),
                new ProductPair(products.get(7), 80),
                new ProductPair(products.get(8), 80)
        ))));

        suppliers.add(new Supplier(locations.get(7), new LinkedList<>(List.of(
                new ProductPair(products.get(5), 150),
                new ProductPair(products.get(0), 200)
        ))));

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