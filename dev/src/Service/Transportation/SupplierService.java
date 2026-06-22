package Service.Transportation;

import DAO.LocationDAO;
import DTO.LocationDTO;
import Domain.Transportation.Supplier;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SupplierService {
    private final List<Supplier> suppliers;
    private final LocationService locationService;
    private final LocationDAO locationDAO;

    public SupplierService(LocationService locationService, LocationDAO locationDAO) {
        this.locationService = locationService;
        this.suppliers = new ArrayList<>();
        this.locationDAO = locationDAO;
    }
    public void loadSuppliersFromDB() throws SQLException {
        suppliers.addAll(locationDAO.loadAllSuppliersAsEmpty(locationService));
    }

    public void addSupplier(String addr,String phone,String contact, Map<Integer, Integer> productMap) throws SQLException {
        int locationId = locationService.addLocation(addr,phone,contact);
        LocationDTO locationDTO = new LocationDTO(locationId,contact,addr,phone,false,true);
        Supplier supplier = new Supplier(locationService.getLocation(locationId), productMap);
        suppliers.add(supplier);
        locationDAO.addLocation(locationDTO);
        //TODO add rows to the supplier allocation DB, according to the map received
    }

    public List<Integer> getSupplierIds() {
        List<Integer> supplierIds = new ArrayList<>();
        for (Supplier supplier : suppliers)
            supplierIds.add(supplier.getLocationId());

        return supplierIds;
    }

    public List<Integer> getProductIds(int locationId) {
        for (Supplier supplier : suppliers)
            if (supplier.getLocationId() == locationId)
                return supplier.getProductIds();

        throw new IllegalArgumentException("Supplier not found at location: " + locationId);
    }

    public int getProductStock(int locationId, int productId) {
        for (Supplier supplier : suppliers)
            if (supplier.getLocationId() == locationId)
                return supplier.getProductStock(productId);

        throw new IllegalArgumentException("Supplier not found at location: " + locationId);
    }

    public void handleShipment(int supplierId, Map<Integer, Integer> itemsToLoad) {
        for (Supplier supplier : suppliers)
            if (supplier.getLocationId() == supplierId) {
                supplier.handleShipment(itemsToLoad);
                return;
            }

        throw new IllegalArgumentException("Supplier not found at location: " + supplierId);
    }

    public String getSupplierName(int locationId) {
        for (Supplier supplier : suppliers)
            if (supplier.getLocationId() == locationId)
                return supplier.getName();

        throw new IllegalArgumentException("Supplier not found at location: " + locationId);
    }

    public String getSupplierDisplay(int supplierId) {
        for (Supplier supplier : suppliers)
            if (supplier.getLocationId() == supplierId)
                return supplier.toString();

        throw new IllegalArgumentException("Supplier not found at location: " + supplierId);
    }

    public void addStock(Integer key, Integer value, int supplierId) {
        for (Supplier supplier : suppliers)
            if (supplier.getLocationId() == supplierId) {
                supplier.addStock(key, value);
                return;
            }

        throw new IllegalArgumentException("Supplier not found at location: " + supplierId);
    }

    public void resupplySupplier(int sId, int pId, int qty) {
        for (Supplier supplier : suppliers)
            if (supplier.getLocationId() == sId)
                supplier.addStock(pId, qty);

        throw new IllegalArgumentException("Supplier not found at location: " + sId);
    }
}
