package Service.Transportation;

import DAO.Transportation.SupplierAllocationDAO;
import DTO.Transportation.SupplierAllocationDTO;
import Domain.Transportation.Location;
import Domain.Transportation.Supplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplierService {
    private final List<Supplier> suppliers;
    private final LocationService locationService;
    private final SupplierAllocationDAO allocationDAO;

    public SupplierService(LocationService locationService, SupplierAllocationDAO allocationDAO) {
        this.locationService = locationService;
        this.allocationDAO = allocationDAO;
        this.suppliers = new ArrayList<>();
    }

    public void loadSuppliersFromDB() {
        suppliers.clear();
        List<Location> supplierLocations = locationService.loadAllSuppliers();
        for (Location loc : supplierLocations) {
            List<SupplierAllocationDTO> dtoList = allocationDAO.getAllocations(loc.id());

            Map<Integer, Integer> allocations = new HashMap<>();
            for (SupplierAllocationDTO dto : dtoList) {
                allocations.put(dto.productID(), dto.amountOfProduct());
            }

            suppliers.add(new Supplier(loc, allocations));
        }
    }

    public void addSupplier(String addr, String phone, String contact, Map<Integer, Integer> productMap) {
        int locationId = locationService.addSupplierLocation(addr, phone, contact);
        Location newLocation = locationService.getLocation(locationId);

        for (Map.Entry<Integer, Integer> entry : productMap.entrySet()) {
            allocationDAO.addAllocation(new SupplierAllocationDTO(locationId, entry.getKey(), entry.getValue()));
        }

        Supplier supplier = new Supplier(newLocation, productMap);
        suppliers.add(supplier);
    }

    public List<Integer> getSupplierIds() {
        List<Integer> supplierIds = new ArrayList<>();
        for (Supplier supplier : suppliers)
            supplierIds.add(supplier.getLocationId());

        return supplierIds;
    }

    public List<Integer> getProductIds(int locationId) {
        return getSupplier(locationId).getProductIds();
    }

    public int getProductStock(int locationId, int productId) {
        return getSupplier(locationId).getProductStock(productId);
    }

    public void handleShipment(int locationId, Map<Integer, Integer> itemsToLoad) {
        Supplier supplier = getSupplier(locationId);
        supplier.handleShipment(itemsToLoad);
        for (Map.Entry<Integer, Integer> entry : itemsToLoad.entrySet()) {
            int updatedStock = supplier.getProductStock(entry.getKey());
            allocationDAO.updateAllocation(new SupplierAllocationDTO(locationId, entry.getKey(), updatedStock));
        }
    }

    public String getSupplierName(int locationId) {
        return getSupplier(locationId).getLocation().contactName();
    }

    public String getSupplierDisplay(int supplierId) {
        return getSupplier(supplierId).toString();
    }

    public void resupplySupplier(int locationId, int productId, int amount) {
        Supplier supplier = getSupplier(locationId);
        supplier.addStock(productId, amount);
        int updatedStock = supplier.getProductStock(productId);
        allocationDAO.updateAllocation(new SupplierAllocationDTO(locationId, productId, updatedStock));
    }


    private Supplier getSupplier(int locationId) {
        for (Supplier supplier : suppliers)
            if (supplier.getLocationId() == locationId)
                return supplier;

        throw new IllegalArgumentException("Supplier not found at location: " + locationId);
    }

    public void checkAvailability(int supplierId, Map<Integer, Integer> itemsToLoad) {
        getSupplier(supplierId).checkAvailability(itemsToLoad);
    }
}