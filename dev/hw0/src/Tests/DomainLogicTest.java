package Tests;

import Domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DomainLogicTest {

    @BeforeEach
    public void setUp() {
        TestData.resetAndInit();
    }

    @Test
    @DisplayName("Driver: License level validation")
    void testDriverLicenseAgainstTruck() {
        Driver alice = TestData.Drivers.Alice;
        Driver david = TestData.Drivers.David;
        Truck heavyTruck = TestData.Trucks.ScaniaR500;

        assertFalse(heavyTruck.getMinLicense() <= alice.license(),
                "Alice (License 1) should NOT be able to drive a Level 3 Truck");

        assertTrue(heavyTruck.getMinLicense() <= david.license(),
                "David (License 3) SHOULD be able to drive a Level 3 Truck");
    }

    @Test
    @DisplayName("ProductPair: Object correctly holds product and amount")
    void testProductPairCreation() {
        Product apple = TestData.Products.Apple;
        ProductPair pair = new ProductPair(apple, 50);

        assertEquals(50, pair.getAmount(), "The amount should be correctly initialized to 50");
        assertEquals("Apple", pair.product.name(), "The product inside the pair should be Apple");
    }

    @Test
    @DisplayName("Supplier: Correctly initializes available products")
    void testSupplierInitialStock() {
        Supplier telAvivSupplier = TestData.allSuppliers.get(0);

        assertFalse(telAvivSupplier.productsAvailable().isEmpty(), "Supplier should not be empty");

        ProductPair firstProduct = telAvivSupplier.productsAvailable().getFirst();
        assertEquals("Apple", firstProduct.product.name(), "First product should be Apple");
        assertEquals(100, firstProduct.getAmount(), "Initial amount of Apples should be 100");
    }

    @Test
    @DisplayName("Truck: Configuration and attributes are set properly")
    void testTruckAttributes() {
        Truck lightTruck = TestData.Trucks.IsuzuSumo;

        assertNotNull(lightTruck.getModel(), "Truck model should not be null");
        assertTrue(lightTruck.getMaxWeight() > 0, "Truck max weight must be strictly positive");
    }
}