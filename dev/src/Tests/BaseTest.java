package Tests;
import Domain.CompanyManager;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTest {

    protected CompanyManager companyManager;

    @BeforeEach
    public void setUp() {

        TestData.resetAndInit();

        CompanyManager.resetInstance();

        companyManager = CompanyManager.getInstance(
                TestData.allTrucks,
                TestData.allDrivers,
                TestData.allSuppliers,
                TestData.allLocations,
                TestData.allBranchManagers
        );
    }
}