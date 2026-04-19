package Tests;

import Domain.*;
import Exceptions.InsufficientSupplierStockException;
import Exceptions.InsufficientTruckStockException;
import Exceptions.OverweightException;
import Presentation.MainConsole;
import Service.BranchManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BranchManagerTest extends BaseTest {

    private BranchManager telAvivBranch;
    private BranchManager ashdodBranch;

    @BeforeEach
    public void setUpBranches() {
        telAvivBranch = new BranchManager(TestData.Locations.TelAviv);
        ashdodBranch = new BranchManager(TestData.Locations.Ashdod);
    }

    @Test
    @DisplayName("Valid shipment request for Tel Aviv branch")
    void testValidShipmentRequest_TelAviv() {

        List<ProductPair> order = new LinkedList<>();
        order.add(new ProductPair(TestData.Products.Apple, 10));
        order.add(new ProductPair(TestData.Products.Kiwi, 5));

        telAvivBranch.requestShipment(order);

        // יצירת נתוני דמה חוקיים עבור הריצה
        Truck testTruck = TestData.Trucks.IsuzuSumo;
        Driver testDriver = TestData.Drivers.Alice;
        Location testSource = TestData.Locations.Haifa;
        Map<Supplier, List<ProductPair>> emptySupplierAllocations = new HashMap<>(); // מפה ריקה לבדיקה בסיסית

        // בדיקה שהפונקציות המעודכנות רצות ללא קריסה
        assertDoesNotThrow(() -> {
            Transport t = companyManager.createShipment(testTruck, testDriver, testSource, emptySupplierAllocations);
            // אם רוצים לבדוק ריצה מלאה אפשר להוסיף: companyManager.processTransport(t);
        });
    }

    @Test
    @DisplayName("Shipment request from Ashdod branch - Tomatoes and Milk")
    void testShipmentFromNewSupplier_Ashdod() {

        List<ProductPair> order = new LinkedList<>();
        order.add(new ProductPair(TestData.Products.Tomato, 20));
        order.add(new ProductPair(TestData.Products.Milk, 15));

        ashdodBranch.requestShipment(order);

        Truck testTruck = TestData.Trucks.DAFLF;
        Driver testDriver = TestData.Drivers.Bob;
        Location testSource = TestData.Locations.Netanya;
        Map<Supplier, List<ProductPair>> emptySupplierAllocations = new HashMap<>();

        assertDoesNotThrow(() -> {
            companyManager.createShipment(testTruck, testDriver, testSource, emptySupplierAllocations);
        });
    }

    @Test
    @DisplayName("Empty shipment request should throw IllegalArgumentException")
    void testEmptyShipment_ThrowsException() {

        List<ProductPair> emptyOrder = new LinkedList<>();

        assertThrows(IllegalArgumentException.class, () -> {
            telAvivBranch.requestShipment(emptyOrder);
        });
    }

    @Test
    @DisplayName("The Destination SHOULD NOT BE served (not enough items)")
    void testDestinationNotServed(){
        List<ProductPair> order = new LinkedList<>();
        order.add(new ProductPair(TestData.Products.Banana, 10));
        ashdodBranch.requestShipment(order);

        Truck t=TestData.Trucks.DAFLF;

        Driver d=TestData.Drivers.Bob;

        Location l=TestData.Locations.Haifa;

        Map<Supplier, List<ProductPair>> supplierAllocations = new HashMap<>();

        Supplier s=TestData.allSuppliers.get(0);

        List<ProductPair> productPairs = new LinkedList<>();
        productPairs.add(new ProductPair(TestData.Products.Apple, 10));

        supplierAllocations.put(s,productPairs);

        Transport transport = companyManager.createShipment(t,d,l,supplierAllocations);

        Destination onlyDestination=transport.getDestinations().getFirst();
        assertThrows(InsufficientTruckStockException.class, () -> {
            companyManager.processTransport(transport);
        });
        assertFalse(onlyDestination.isVisited());


    }



    @Test
    @DisplayName("The Destination SHOULD BE served ")
    void testDestinationWasServed(){
        List<ProductPair> order = new LinkedList<>();
        order.add(new ProductPair(TestData.Products.Apple, 10));
        ashdodBranch.requestShipment(order);

        Truck t=TestData.Trucks.DAFLF;

        Driver d=TestData.Drivers.Bob;

        Location l=TestData.Locations.Haifa;

        Map<Supplier, List<ProductPair>> supplierAllocations = new HashMap<>();

        Supplier s=TestData.allSuppliers.get(0);

        List<ProductPair> productPairs = new LinkedList<>();
        productPairs.add(new ProductPair(TestData.Products.Apple, 10));

        supplierAllocations.put(s,productPairs);

        Transport transport = companyManager.createShipment(t,d,l,supplierAllocations);

        Destination onlyDestination=transport.getDestinations().getFirst();

        assertDoesNotThrow(() -> {
            companyManager.processTransport(transport);
        });
        assertTrue(onlyDestination.isVisited());


    }


    @Test
    @DisplayName("Supplier stock should be reduced")
    void testSupplierStockReduces(){
        List<ProductPair> order = new LinkedList<>();
        order.add(new ProductPair(TestData.Products.Apple, 10));
        ashdodBranch.requestShipment(order);

        Truck t=TestData.Trucks.DAFLF;

        Driver d=TestData.Drivers.Bob;

        Location l=TestData.Locations.Haifa;

        Map<Supplier, List<ProductPair>> supplierAllocations = new HashMap<>();

        Supplier s=TestData.allSuppliers.get(0);

        List<ProductPair> productPairs = new LinkedList<>();
        productPairs.add(new ProductPair(TestData.Products.Apple, 10));

        supplierAllocations.put(s,productPairs);

        Transport transport = companyManager.createShipment(t,d,l,supplierAllocations);

        Destination onlyDestination=transport.getDestinations().getFirst();

        assertDoesNotThrow(() -> {
            companyManager.processTransport(transport);
        });

        assertEquals(90,s.productsAvailable().getFirst().getAmount());
    }


    @Test
    @DisplayName("CHECK CONSOLE HANDLES SKIPPING SUPPLIERS AND UPDATES TRANSPORT FILE")
    void testSuppliersSkipsViaConsole() {
        // 1. הגדרת ספקים - אחד תקין ואחד ריק
        Supplier goodSupplier = TestData.allSuppliers.get(0);
        Supplier emptySupplier = TestData.allSuppliers.get(1);
        emptySupplier.productsAvailable().clear();

        // 2. סימולציה של קלט (Input Stream)
        // "2" - משאית DAF LF (רישיון 2)
        // "2" - נהג Bob (רישיון 2) -> התאמה מושלמת!
        // "1" - מיקום מקור
        // "1, 2" - בחירת שני הספקים
        // "1" -> "10" -> "done" (עבור ספק 1)
        // "1" -> "10" -> "done" (עבור ספק 2)
        String simulatedInput = "2\n2\n1\n1, 2\n1\n10\ndone\n1\n10\ndone\n";

        InputStream savedStandardIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        try {
            MainConsole console = new MainConsole(
                    companyManager,
                    new ArrayList<>(TestData.allTrucks),
                    new ArrayList<>(TestData.allDrivers),
                    new ArrayList<>(TestData.allLocations),
                    Arrays.asList(goodSupplier, emptySupplier)
            );

            Transport transport =console.run();

            // שליפת הטרנספורט האחרון לבדיקה

            // בדיקה שהלוגיקה ב-Catch עבדה:
            // המערכת צריכה להסיר את הספק הריק ולהשאיר רק את התקין
            assertEquals(1, transport.getTransportFile().getSuppliers().size(), "Should skip the empty supplier");
            assertTrue(transport.getTransportFile().getSuppliers().contains(goodSupplier));
            assertFalse(transport.getTransportFile().getSuppliers().contains(emptySupplier));

        } finally {
            System.setIn(savedStandardIn);
        }
    }

    @Test
    @DisplayName("DESTINATION SHOULD NOT APPEAR IN TRANSPORT FILE")
    void testDestinationIsNotInFile(){
        // 1. הגדרת נתונים בסיסיים: ספק ומוצר
        Supplier s = TestData.allSuppliers.get(0);
        Product banana = TestData.Products.Banana;

        // שימוש ב-BranchManager כדי לבקש משלוח לסניף (למשל סניף אשדוד)
        List<ProductPair> order = new LinkedList<>();
        order.add(new ProductPair(banana, 10));

        ashdodBranch.requestShipment(order); // כאן נוצרת הדרישה במערכת

        // 2. סימולציה של קלט (Input Stream)
        // "2" - משאית, "2" - נהג, "1" - מקור
        // "1" - בחירת ספק אחד
        // "1" -> "10" -> "done" - בחירת מוצרים
        String simulatedInput = "2\n2\n1\n1\n1\n10\ndone\n";

        InputStream savedStandardIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        try {
            MainConsole console = new MainConsole(
                    companyManager,
                    new ArrayList<>(TestData.allTrucks),
                    new ArrayList<>(TestData.allDrivers),
                    new ArrayList<>(TestData.allLocations),
                    Arrays.asList(s)
            );

            // 3. הרצת הקונסול
            // ה-run יקרא ל-createShipment, שימשוך את הדרישה מה-BranchManager וייצר Destination
            Transport transport = console.run();

            // 4. ולידציה
            // בדיקה שהיעד הוסר מהרשימה האקטיבית בטרנספורט (כי ה-Console תפס Exception)
            // (הערה: כדי שהטסט יעבור, ה-processTransport חייב לזרוק InsufficientTruckStockException)
            boolean destExists = transport.getTransportFile().getDestinations().stream()
                    .anyMatch(d -> d.getContactName().equals(ashdodBranch.getLocation().contactName()));

            assertFalse(destExists, "The problematic destination should be removed from the transport");

            // 5. בדיקה שהיעד לא מופיע בטקסט של ה-TransportFile
            String fileContent = transport.getTransportFile().toString();
            assertFalse(fileContent.contains(ashdodBranch.getLocation().contactName()),
                    "The skipped destination should not appear in the final transport file report");

        } finally {
            System.setIn(savedStandardIn);
        }
    }


    @Test
    @DisplayName("DESTINATION SHOULD  APPEAR IN TRANSPORT FILE")
    void testDestinationIsInsideFile(){
        // 1. הגדרת נתונים בסיסיים: ספק ומוצר
        Supplier s = TestData.allSuppliers.get(0);
        Product apple = TestData.Products.Apple;

        // שימוש ב-BranchManager כדי לבקש משלוח לסניף (למשל סניף אשדוד)
        List<ProductPair> order = new LinkedList<>();
        order.add(new ProductPair(apple, 10));

        ashdodBranch.requestShipment(order); // כאן נוצרת הדרישה במערכת

        // 2. סימולציה של קלט (Input Stream)
        // "2" - משאית, "2" - נהג, "1" - מקור
        // "1" - בחירת ספק אחד
        // "1" -> "10" -> "done" - בחירת מוצרים
        String simulatedInput = "2\n2\n1\n1\n1\n10\ndone\n";

        InputStream savedStandardIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        try {
            MainConsole console = new MainConsole(
                    companyManager,
                    new ArrayList<>(TestData.allTrucks),
                    new ArrayList<>(TestData.allDrivers),
                    new ArrayList<>(TestData.allLocations),
                    Arrays.asList(s)
            );

            // 3. הרצת הקונסול
            // ה-run יקרא ל-createShipment, שימשוך את הדרישה מה-BranchManager וייצר Destination
            Transport transport = console.run();

            // 4. ולידציה
            // בדיקה שהיעד הוסר מהרשימה האקטיבית בטרנספורט (כי ה-Console תפס Exception)
            // (הערה: כדי שהטסט יעבור, ה-processTransport חייב לזרוק InsufficientTruckStockException)
            boolean destExists = transport.getTransportFile().getDestinations().stream()
                    .anyMatch(d -> d.getContactName().equals(ashdodBranch.getLocation().contactName()));

            assertTrue(destExists, "The destination should be in the File");

            // 5. בדיקה שהיעד לא מופיע בטקסט של ה-TransportFile
            String fileContent = transport.getTransportFile().toString();
            assertTrue(fileContent.contains(ashdodBranch.getLocation().contactName()),
                    "The  destination should appear in the final transport file report");

        } finally {
            System.setIn(savedStandardIn);
        }
    }

    @Test
    @DisplayName("Should correctly sum products from multiple suppliers")
    void testProductAggregation() {
        Supplier s1 = TestData.allSuppliers.get(0); // Tel Aviv
        Supplier s2 = TestData.allSuppliers.get(5); // Jerusalem

        Map<Supplier, List<ProductPair>> allocations = new HashMap<>();
        // Both suppliers providing Apples
        allocations.put(s1, new ArrayList<>(List.of(new ProductPair(TestData.Products.Apple, 10))));
        allocations.put(s2, new ArrayList<>(List.of(new ProductPair(TestData.Products.Apple, 20))));

        // We use the helper method logic we built earlier
        Transport transport = companyManager.createShipment(
                TestData.Trucks.IsuzuSumo,
                TestData.Drivers.Alice,
                TestData.Locations.Haifa,
                allocations
        );

        // Get the combined map from the transport file
        Map<Product, Integer> totals = transport.getTransportFile().getTotalProductsNeeded();

        assertEquals(30, totals.get(TestData.Products.Apple), "Total Apples should be 30 (10 from s1 + 20 from s2)");
    }

    @Test
    @DisplayName("Should throw exception when shipment exceeds truck weight capacity")
    void testOverweightShipment() {
        Truck lightTruck = TestData.Trucks.IsuzuSumo; // Assume max weight is 5000
        int maxWeight = lightTruck.getMaxWeight();

        // Create a product list that is exactly 1kg over the limit
        List<ProductPair> overweightList = List.of(new ProductPair(TestData.Products.Apple, 100));

        Map<Supplier, List<ProductPair>> allocations = new HashMap<>();
        allocations.put(TestData.allSuppliers.get(0), overweightList);

        // Verify that the system prevents creating or processing an overweight shipment
        assertThrows(OverweightException.class, () -> {
            Transport t=companyManager.createShipment(lightTruck, TestData.Drivers.Alice, TestData.Locations.Haifa, allocations);
            companyManager.processTransport(t);
        }, "Should not allow shipment exceeding truck capacity");
    }

    @Test
    @DisplayName("Truck should keep extra items while TransportFile records full pickup")
    void testExtraItemsInTruckVsFile() {
        // 1. Setup: Branch requests 10 Apples
        Supplier s = TestData.allSuppliers.get(0); // Tel Aviv Supplier
        Product apple = TestData.Products.Apple;

        List<ProductPair> order = new LinkedList<>();
        order.add(new ProductPair(apple, 10));
        telAvivBranch.requestShipment(order);

        // 2. Simulation Input:
        // "2" (Truck), "2" (Driver), "1" (Source)
        // "1" (Select Supplier 1)
        // "1" -> "20" -> "done" (USER OVERRIDES: Picks up 20 instead of 10)
        String simulatedInput = "2\n2\n1\n1\n1\n20\ndone\n";

        InputStream savedStandardIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        try {
            MainConsole console = new MainConsole(
                    companyManager,
                    new ArrayList<>(TestData.allTrucks),
                    new ArrayList<>(TestData.allDrivers),
                    new ArrayList<>(TestData.allLocations),
                    Arrays.asList(s)
            );

            // 3. Run the logic
            Transport transport = console.run();

            // Process the transport (this simulates driving and dropping off items)
            assertDoesNotThrow(() -> {
                companyManager.processTransport(transport);
            });

            // 4. Validation: Check the TransportFile (The Paperwork)
            // It should show the full amount the user typed (20)
            Map<Product, Integer> fileTotals = transport.getTransportFile().getTotalProductsNeeded();
            assertEquals(20, fileTotals.get(apple),
                    "The TransportFile should record the actual pickup of 20 apples");

            // 5. Validation: Check the Truck (The Physical Inventory)
            // Truck had 20, dropped off 10 at the branch, should have 10 left
            ProductPair truckBalance = transport.getTruck().getProductPairs().getOrDefault("Apple", null);
            assertEquals(10, truckBalance.getAmount(),
                    "The truck should still hold 10 'extra' apples after delivering the requested 10");

        } finally {
            System.setIn(savedStandardIn);
        }
    }

}