package Tests;

import Domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DomainLogicTest {

    // אנחנו לא צריכים את BaseTest פה כי אנחנו בודקים רק אובייקטים קטנים ונקודתיים
    @BeforeEach
    public void setUp() {
        // מאפסים את הנתונים לפני כל טסט כדי למנוע התנגשויות
        TestData.resetAndInit();
    }

    @Test
    @DisplayName("Driver: License level validation")
    void testDriverLicenseAgainstTruck() {
        Driver alice = TestData.Drivers.Alice; // יש לה רישיון רמה 1
        Driver david = TestData.Drivers.David; // יש לו רישיון רמה 3
        Truck heavyTruck = TestData.Trucks.ScaniaR500; // דורשת רישיון מינימלי רמה 3

        // בדיקה שנהג עם רישיון נמוך מדי לא יכול לנהוג במשאית
        assertFalse(heavyTruck.getMinLicense() <= alice.license(),
                "Alice (License 1) should NOT be able to drive a Level 3 Truck");

        // בדיקה שנהג עם רישיון מתאים כן יכול לנהוג
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
        // שולפים את הספק מתל אביב (שאמור להכיל 100 תפוחים לפי TestData)
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