package Tests;

import Domain.*;
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

        Truck testTruck = TestData.Trucks.IsuzuSumo;
        Driver testDriver = TestData.Drivers.Alice;
        Location testSource = TestData.Locations.Haifa;
        Map<Supplier, List<ProductPair>> emptySupplierAllocations = new HashMap<>();

        assertDoesNotThrow(() -> {
            Transport t = companyManager.createShipment(testTruck, testDriver, testSource, emptySupplierAllocations);
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
        Supplier goodSupplier = TestData.allSuppliers.get(0);
        Supplier emptySupplier = TestData.allSuppliers.get(1);
        emptySupplier.productsAvailable().clear();

        String simulatedInput = "2\n2\n1\n1, 2\n1\n10\ndone\n1\n10\ndone\n";

        InputStream savedStandardIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        try {
            MainConsole console = new MainConsole(
                    companyManager
            );

            Transport transport = console.initiateShipment();;

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
        Supplier s = TestData.allSuppliers.get(0);
        Product banana = TestData.Products.Banana;

        List<ProductPair> order = new LinkedList<>();
        order.add(new ProductPair(banana, 10));

        ashdodBranch.requestShipment(order);

        String simulatedInput = "2\n2\n1\n1\n1\n10\ndone\n";

        InputStream savedStandardIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        try {
            MainConsole console = new MainConsole(
                    companyManager
            );

            Transport transport = console.initiateShipment();;

            assertFalse(transport.getTransportFile().toString().contains("- " +
                            ashdodBranch.getLocation().contactName() + " | Address: " +
                            ashdodBranch.getLocation().address()),
                    "The skipped destination should not appear in the final transport file report");

        } finally {
            System.setIn(savedStandardIn);
        }
    }


    @Test
    @DisplayName("DESTINATION SHOULD APPEAR IN TRANSPORT FILE")
    void testDestinationIsInsideFile(){
        Supplier s = TestData.allSuppliers.get(0);
        Product apple = TestData.Products.Apple;

        List<ProductPair> order = new LinkedList<>();
        order.add(new ProductPair(apple, 10));

        ashdodBranch.requestShipment(order);

        String simulatedInput = "2\n2\n1\n1\n1\n10\ndone\n";

        InputStream savedStandardIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        try {
            MainConsole console = new MainConsole(
                    companyManager
            );

            Transport transport = console.initiateShipment();;

            boolean destExists = transport.getTransportFile().getDestinations().stream()
                    .anyMatch(d -> d.getContactName().equals(ashdodBranch.getLocation().contactName()));

            assertTrue(destExists, "The destination should be in the File");

            String fileContent = transport.getTransportFile().toString();
            assertTrue(transport.getTransportFile().toString().contains("- " +
                    ashdodBranch.getLocation().contactName() + " | Address: " +
                    ashdodBranch.getLocation().address()),
                    "The destination should appear in the final transport file report");

        } finally {
            System.setIn(savedStandardIn);
        }
    }

    @Test
    @DisplayName("Should correctly sum products from multiple suppliers")
    void testProductAggregation() {
        Supplier s1 = TestData.allSuppliers.get(0);
        Supplier s2 = TestData.allSuppliers.get(5);

        Map<Supplier, List<ProductPair>> allocations = new HashMap<>();
        allocations.put(s1, new ArrayList<>(List.of(new ProductPair(TestData.Products.Apple, 10))));
        allocations.put(s2, new ArrayList<>(List.of(new ProductPair(TestData.Products.Apple, 20))));

        Transport transport = companyManager.createShipment(
                TestData.Trucks.IsuzuSumo,
                TestData.Drivers.Alice,
                TestData.Locations.Haifa,
                allocations
        );

        Map<Product, Integer> totals = transport.getTransportFile().getTotalProductsNeeded();

        assertEquals(30, totals.get(TestData.Products.Apple), "Total Apples should be 30 (10 from s1 + 20 from s2)");
    }

    @Test
    @DisplayName("Should throw exception when shipment exceeds truck weight capacity")
    void testOverweightShipment() {
        Truck lightTruck = TestData.Trucks.IsuzuSumo;
        int maxWeight = lightTruck.getMaxWeight();

        List<ProductPair> overweightList = List.of(new ProductPair(TestData.Products.Apple, 100));

        Map<Supplier, List<ProductPair>> allocations = new HashMap<>();
        allocations.put(TestData.allSuppliers.get(0), overweightList);

        assertThrows(OverweightException.class, () -> {
            Transport t=companyManager.createShipment(lightTruck, TestData.Drivers.Alice, TestData.Locations.Haifa, allocations);
            companyManager.processTransport(t);
        }, "Should not allow shipment exceeding truck capacity");
    }

    @Test
    @DisplayName("Truck should keep extra items while TransportFile records full pickup")
    void testExtraItemsInTruckVsFile() {
        Supplier s = TestData.allSuppliers.get(0);
        Product apple = TestData.Products.Apple;

        List<ProductPair> order = new LinkedList<>();
        order.add(new ProductPair(apple, 10));
        telAvivBranch.requestShipment(order);

        String simulatedInput = "2\n2\n1\n1\n1\n20\ndone\n";

        InputStream savedStandardIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        try {
            MainConsole console = new MainConsole(
                    companyManager
            );

            Transport transport = console.initiateShipment();;

            assertDoesNotThrow(() -> {
                companyManager.processTransport(transport);
            });

            Map<Product, Integer> fileTotals = transport.getTransportFile().getTotalProductsNeeded();
            assertEquals(20, fileTotals.get(apple),
                    "The TransportFile should record the actual pickup of 20 apples");
        } finally {
            System.setIn(savedStandardIn);
        }
    }

}