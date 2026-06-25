package Domaina.Transportation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DAO.Transportation.*;
import DTO.Transportation.*;
import Service.Transportation.*;
import Service.Workers.ShiftJobsService;
import Service.Workers.WorkersService;
import Domain.Transportation.Location;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TransporationUnitTests {

    // Real Services configured to run purely in memory via our Fake DAOs
    private LocationService locationService;
    private ProductCatalogService productCatalogService;
    private TruckService truckService;
    private SupplierService supplierService;
    private RequestService requestService;
    private BranchService branchService;
    private TransportManagerService transportService;

    // In-memory structural fakes
    private FakeLocationDAO fakeLocationDAO;
    private FakeProductDAO fakeProductDAO;
    private FakeTruckDAO fakeTruckDAO;
    private FakeSupplierAllocationDAO fakeSupplierAllocationDAO;
    private FakeRequestDAO fakeRequestDAO;
    private FakeTransportFileDAO fakeTransportFileDAO;
    private FakeWorkersService fakeWorkersService;
    private FakeShiftJobsService fakeShiftJobsService;

    @BeforeEach
    public void setUp() {
        // Initialize our manual fakes
        fakeLocationDAO = new FakeLocationDAO();
        fakeProductDAO = new FakeProductDAO();
        fakeTruckDAO = new FakeTruckDAO();
        fakeSupplierAllocationDAO = new FakeSupplierAllocationDAO();
        fakeRequestDAO = new FakeRequestDAO();
        fakeTransportFileDAO = new FakeTransportFileDAO();
        fakeWorkersService = new FakeWorkersService();
        fakeShiftJobsService = new FakeShiftJobsService();

        // Inject the fakes directly into the service layer
        locationService = new LocationService(fakeLocationDAO);
        productCatalogService = new ProductCatalogService(fakeProductDAO);
        truckService = new TruckService(productCatalogService, fakeTruckDAO);
        supplierService = new SupplierService(locationService, fakeSupplierAllocationDAO);
        requestService = new RequestService(locationService, fakeRequestDAO);
        branchService = new BranchService(locationService);

        transportService = new TransportManagerService(
                truckService, supplierService, requestService,
                fakeWorkersService, fakeShiftJobsService, fakeTransportFileDAO
        );
    }

    // ==========================================
    // 5 TESTS FOR: TransportManagerService
    // ==========================================

    @Test
    public void testTransportService_CreateTransportLifecycleFlow() {
        fakeWorkersService.stubbedName = "John Doe";
        fakeWorkersService.stubbedLicense = 3;

        // Populate the in-memory truck service first so it registers ID 1
        truckService.addTruck(123, "Volvo", 5000, 10000, 3);

        Map<Integer, Map<Integer, Integer>> allocations = new HashMap<>();

        // Before creating transport, getTruckId should throw because transport is null
        assertThrows(IllegalArgumentException.class, () -> transportService.getTruckId());

        transportService.createTransport(1, 10, 100, allocations, "TruckInfo", "SourceInfo");

        assertEquals(1, transportService.getTruckId());
        assertEquals(10, transportService.getDriverId());
    }

    @Test
    public void testTransportService_FinishShipmentClearsState() {
        fakeWorkersService.stubbedName = "Driver Bob";
        fakeWorkersService.stubbedLicense = 2;

        // Pre-populate truck with ID 1 in memory
        truckService.addTruck(456, "Scania", 6000, 12000, 2);

        transportService.createTransport(1, 5, 100, new HashMap<>(), "Truck", "Src");
        transportService.finishShipment();

        assertTrue(fakeTransportFileDAO.addTransportFileCalled);
        assertThrows(IllegalArgumentException.class, () -> transportService.getTruckId());
    }

    @Test
    public void testTransportService_ReplaceTruckUpdatesLogistics() {
        fakeWorkersService.stubbedName = "Driver";

        // Populate trucks so we have valid IDs 1 and 2 in memory
        truckService.addTruck(111, "Old Truck", 4000, 8000, 1);  // ID 1
        truckService.addTruck(222, "New Truck", 5000, 10000, 1); // ID 2

        transportService.createTransport(1, 1, 100, new HashMap<>(), "T1", "S");

        transportService.replaceTruck(2);
        assertEquals(2, transportService.getTruckId());
    }

    @Test
    public void testTransportService_LoadCounterIncrementsDAOValue() {
        fakeTransportFileDAO.stubbedMaxFileNumber = 42;
        transportService.loadCountFromDB();
        assertTrue(fakeTransportFileDAO.getMaxFileNumberCalled);
    }

    @Test
    public void testTransportService_OperationsThrowExceptionWithoutActiveTransport() {
        assertThrows(IllegalArgumentException.class, () -> transportService.startShipment());
        assertThrows(IllegalArgumentException.class, () -> transportService.skipRequest("No cargo"));
    }

    // ==========================================
    // 2 TESTS FOR: BranchService
    // ==========================================

    @Test
    public void testBranchService_AddBranchRegistersCorrectly() {
        branchService.addBranch("Tel Aviv 1", "050-123", "Avi");
        assertEquals(1, branchService.getBranches().size());
    }

    @Test
    public void testBranchService_RemoveBranchCleansUpCollections() {
        branchService.addBranch("Haifa 2", "054-987", "Eli"); // Registered under ID 1 in memory
        branchService.removeBranch(1);
        assertTrue(branchService.getBranches().isEmpty());
    }

    // ==========================================
    // 2 TESTS FOR: LocationService
    // ==========================================

    @Test
    public void testLocationService_AddLocationGeneratesSequentialIds() {
        int firstId = locationService.addLocation("Addr1", "Phone1", "Contact1");  // 1
        int secondId = locationService.addLocation("Addr2", "Phone2", "Contact2"); // 2

        assertEquals(1, firstId);
        assertEquals(2, secondId);
    }

    @Test
    public void testLocationService_GetLocationThrowsOnInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> locationService.getLocation(999));
    }

    // ==========================================
    // 2 TESTS FOR: ProductCatalogService
    // ==========================================

    @Test
    public void testProductCatalogService_AddProductStoresInLocalContext() {
        productCatalogService.addProduct("Milk", 1); // Auto-assigned ID 1

        assertEquals(1, productCatalogService.getProductsId().size());
        assertEquals("Milk", productCatalogService.getProductName(1));
    }

    @Test
    public void testProductCatalogService_DisplayFormattingOutput() {
        productCatalogService.addProduct("Bread", 2); // Auto-assigned ID 1
        Map<Integer, Integer> shoppingCart = Map.of(1, 5);

        String display = productCatalogService.getProductsDisplay(shoppingCart);

        assertNotNull(display);
        assertTrue(display.contains("Bread"));
        assertTrue(display.contains("5 units"));
    }

    // ==========================================
    // 2 TESTS FOR: RequestService
    // ==========================================

    @Test
    public void testRequestService_HasRequestsEvaluatesCorrectly() {
        assertFalse(requestService.hasRequests());

        locationService.addLocation("Store 7", "111", "Moni"); // Location ID 1
        requestService.addRequest(1, Map.of(101, 5));

        assertTrue(requestService.hasRequests());
    }

    @Test
    public void testRequestService_ClearRequestsEmptiesState() {
        locationService.addLocation("Store 8", "222", "Gabi"); // Location ID 1
        requestService.addRequest(1, Map.of(102, 2));

        requestService.clearRequests();
        assertFalse(requestService.hasRequests());
    }

    // ==========================================
    // 2 TESTS FOR: SupplierService
    // ==========================================

    @Test
    public void testSupplierService_AddSupplierCreatesTrackingMetrics() {
        Map<Integer, Integer> supplies = Map.of(50, 100);
        supplierService.addSupplier("Industrial Zone 4", "03-555", "Sami", supplies); // Location ID 1

        assertEquals(1, supplierService.getSupplierIds().size());
        assertEquals("Sami", supplierService.getSupplierName(1));
    }

    @Test
    public void testSupplierService_ResupplyIncreasesExistingStock() {
        Map<Integer, Integer> initialStock = new HashMap<>(Map.of(20, 10));
        supplierService.addSupplier("North Pier", "04-888", "Rami", initialStock); // Location ID 1

        supplierService.resupplySupplier(1, 20, 40);
        assertEquals(50, supplierService.getProductStock(1, 20));
    }

    // ==========================================
    // 2 TESTS FOR: TruckService
    // ==========================================

    @Test
    public void testTruckService_AddTruckTracksAvailability() {
        truckService.addTruck(12345, "Volvo FMX", 8000, 15000, 3); // Auto ID 1
        assertEquals(1, truckService.getAvailableTruckIds().size());
    }

    @Test
    public void testTruckService_DriverEligibilityVerifiesLicensing() {
        truckService.addTruck(99999, "Scania Heavy", 10000, 25000, 4); // Auto ID 1

        assertTrue(truckService.isDriverEligible(4, 1));
        assertFalse(truckService.isDriverEligible(2, 1));
    }

    // =========================================================================
    // NO-OP IN-MEMORY FAKE CLASSES FOR DAOS & EXTERNAL SYSTEMS
    // =========================================================================

    private static class FakeLocationDAO extends LocationDAO {
        public FakeLocationDAO() { super(null); }
        @Override public int getHighestLocationID() { return 0; }
        @Override public void addLocation(LocationDTO dto) {}
        @Override public void addBranch(LocationDTO dto) {}
        @Override public void addSupplier(LocationDTO dto) {}
        @Override public List<LocationDTO> loadAllLocations() { return new ArrayList<>(); }
        @Override public List<LocationDTO> loadAllBranches() { return new ArrayList<>(); }
        @Override public List<LocationDTO> loadAllSuppliers() { return new ArrayList<>(); }
        @Override public void removeLocation(int id) {}
    }

    private static class FakeProductDAO extends ProductDAO {
        public FakeProductDAO() { super(null); }
        @Override public int getHighestProductID() { return 0; }
        @Override public void addProduct(ProductDTO dto) {}
        @Override public void removeProduct(int id) {}
        @Override public List<ProductDTO> loadAllProducts() { return new ArrayList<>(); }
    }

    private static class FakeTruckDAO extends TruckDAO {
        public FakeTruckDAO() { super(null); }
        @Override public int getHighestTruckID() { return 0; }
        @Override public void addTruck(TruckDTO dto) {}
        @Override public List<TruckDTO> loadAllTrucks() { return new ArrayList<>(); }
    }

    private static class FakeSupplierAllocationDAO extends SupplierAllocationDAO {
        public FakeSupplierAllocationDAO() { super(null); }
        @Override public List<SupplierAllocationDTO> getAllocations(int locId) { return new ArrayList<>(); }
        @Override public void addAllocation(SupplierAllocationDTO dto) {}
        @Override public void updateAllocation(SupplierAllocationDTO dto) {}
    }

    private static class FakeRequestDAO extends RequestDAO {
        public FakeRequestDAO() { super(null); }
        @Override public Map<RequestDTO, List<ProductFile_ItemsDTO>> loadAllRequests() { return new HashMap<>(); }
        @Override public int getMaxFileNumber() { return 0; }
        @Override public void addProductFileItem(ProductFile_ItemsDTO dto) {}
        @Override public void addProductFile(ProductFileDTO dto) {}
        @Override public void addRequest(RequestDTO dto) {}
        @Override public void setRequestInactive(int locId, int fileNum) {}
    }

    private static class FakeTransportFileDAO extends TransportFileDAO {
        public FakeTransportFileDAO() { super(null); }
        boolean addTransportFileCalled = false;
        boolean getMaxFileNumberCalled = false;
        int stubbedMaxFileNumber = 0;

        @Override
        public void addTransportFile(TransportFileDTO dto) { addTransportFileCalled = true; }
        @Override
        public int getMaxFileNumber() { getMaxFileNumberCalled = true; return stubbedMaxFileNumber; }
    }

    private static class FakeWorkersService extends WorkersService {
        public FakeWorkersService() { super(null); }
        String stubbedName = "Fake Worker";
        int stubbedLicense = 1;
        @Override public String getName(int id) { return stubbedName; }
        @Override public int getLicense(int id) { return stubbedLicense; }
    }

    private static class FakeShiftJobsService extends ShiftJobsService {
        public FakeShiftJobsService() { super(null); }
        @Override public boolean hashShopKeeper(java.time.LocalDate date, boolean isMorning, Location loc) { return true; }
    }
}