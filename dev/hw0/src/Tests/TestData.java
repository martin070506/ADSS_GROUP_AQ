package Tests;
import Domain.*;
import java.util.*;

public class TestData {

    public static class Locations {
        public static Location TelAviv;
        public static Location Holon;
        public static Location Haifa;
        public static Location Netanya;
        public static Location BeerSheva;
        public static Location Jerusalem;
        public static Location Ashdod;
        public static Location PetahTikva;
        public static Location RamatGan;
        public static Location Rehovot;
    }

    public static class Products {
        public static Product Apple;
        public static Product Banana;
        public static Product Orange;
        public static Product Lemon;
        public static Product Pineapple;
        public static Product Kiwi;
        // 4 מוצרים חדשים
        public static Product Tomato;
        public static Product Cucumber;
        public static Product Milk;
        public static Product Bread;
    }

    public static class Trucks {
        public static Truck IsuzuSumo;
        public static Truck DAFLF;
        public static Truck VolvoFH;
        public static Truck MercedesActros;
        public static Truck ScaniaR500;
    }

    public static class Drivers {
        public static Driver Alice;
        public static Driver Bob;
        public static Driver Charlie;
        public static Driver David;
        public static Driver Eve;
    }

    public static List<Location> allLocations = new ArrayList<>();
    public static List<Supplier> allSuppliers = new ArrayList<>();
    public static List<Truck> allTrucks = new ArrayList<>();
    public static List<Driver> allDrivers = new ArrayList<>();

    public static void resetAndInit() {
        Locations.TelAviv = new Location("Tel Aviv", "03-1234567", "Alice");
        Locations.Holon = new Location("Holon", "03-7654321", "Bob");
        Locations.Haifa = new Location("Haifa", "04-1112233", "Charlie");
        Locations.Netanya = new Location("Netanya", "09-4445566", "David");
        Locations.BeerSheva = new Location("Beer Sheva", "08-9998877", "Eve");
        Locations.Jerusalem = new Location("Jerusalem", "02-5556677", "Frank");
        Locations.Ashdod = new Location("Ashdod", "08-2223344", "Grace");
        Locations.PetahTikva = new Location("Petah Tikva", "03-9990011", "Henry");
        Locations.RamatGan = new Location("Ramat Gan", "03-4445556", "Isabella");
        Locations.Rehovot = new Location("Rehovot", "08-7778899", "Jack");

        Products.Apple = new Product("Apple", 150);
        Products.Banana = new Product("Banana", 120);
        Products.Orange = new Product("Orange", 200);
        Products.Lemon = new Product("Lemon", 80);
        Products.Pineapple = new Product("Pineapple", 900);
        Products.Kiwi = new Product("Kiwi", 250);

        Products.Tomato = new Product("Tomato", 10);
        Products.Cucumber = new Product("Cucumber", 8);
        Products.Milk = new Product("Milk", 6);
        Products.Bread = new Product("Bread", 12);

        Trucks.IsuzuSumo = new Truck(101, "Isuzu Sumo", 3500, 7500, 2);
        Trucks.DAFLF = new Truck(102, "DAF LF", 5000, 12000, 2);
        Trucks.VolvoFH = new Truck(103, "Volvo FH", 8000, 26000, 3);
        Trucks.MercedesActros = new Truck(104, "Mercedes Actros", 9500, 32000, 3);
        Trucks.ScaniaR500 = new Truck(105, "Scania R500", 10000, 40000, 3);

        Drivers.Alice = new Driver("Alice", 1);
        Drivers.Bob = new Driver("Bob", 2);
        Drivers.Charlie = new Driver("Charlie", 2);
        Drivers.David = new Driver("David", 3);
        Drivers.Eve = new Driver("Eve", 3);

        allLocations.clear();
        allLocations.addAll(Arrays.asList(
                Locations.TelAviv, Locations.Holon, Locations.Haifa, Locations.Netanya,
                Locations.BeerSheva, Locations.Jerusalem, Locations.Ashdod,
                Locations.PetahTikva, Locations.RamatGan, Locations.Rehovot
        ));

        allTrucks.clear();
        allTrucks.addAll(Arrays.asList(
                Trucks.IsuzuSumo, Trucks.DAFLF, Trucks.VolvoFH, Trucks.MercedesActros, Trucks.ScaniaR500
        ));

        allDrivers.clear();
        allDrivers.addAll(Arrays.asList(
                Drivers.Alice, Drivers.Bob, Drivers.Charlie, Drivers.David, Drivers.Eve
        ));

        allSuppliers.clear();
        allSuppliers.add(new Supplier(Locations.TelAviv, new LinkedList<>(List.of(new ProductPair(Products.Apple, 100)))));
        allSuppliers.add(new Supplier(Locations.Holon, new LinkedList<>(List.of(new ProductPair(Products.Banana, 100)))));
        allSuppliers.add(new Supplier(Locations.Haifa, new LinkedList<>(List.of(new ProductPair(Products.Orange, 100)))));
        allSuppliers.add(new Supplier(Locations.Netanya, new LinkedList<>(List.of(new ProductPair(Products.Lemon, 100)))));
        allSuppliers.add(new Supplier(Locations.BeerSheva, new LinkedList<>(List.of(new ProductPair(Products.Pineapple, 100)))));

        allSuppliers.add(new Supplier(Locations.Jerusalem, new LinkedList<>(List.of(
                new ProductPair(Products.Orange, 50),
                new ProductPair(Products.Lemon, 50),
                new ProductPair(Products.Pineapple, 50)
        ))));

        allSuppliers.add(new Supplier(Locations.Ashdod, new LinkedList<>(List.of(
                new ProductPair(Products.Tomato, 80),
                new ProductPair(Products.Cucumber, 80),
                new ProductPair(Products.Milk, 80)
        ))));
    }
}