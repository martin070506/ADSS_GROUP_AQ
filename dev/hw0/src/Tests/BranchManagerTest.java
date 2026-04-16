package Tests;

import Domain.*;
import Service.BranchManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
            Transport t = companyManager.startShipment(testTruck, testDriver, testSource, emptySupplierAllocations);
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
            companyManager.startShipment(testTruck, testDriver, testSource, emptySupplierAllocations);
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
}