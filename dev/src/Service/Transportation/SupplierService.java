package Service.Transportation;

import DAO.SupplierAllocationDAO;
import Domain.Transportation.Location;
import Domain.Transportation.Supplier;

import java.sql.SQLException;
import java.util.ArrayList;
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
        try {
            for (Location loc : supplierLocations) {
                Map<Integer, Integer> allocations = allocationDAO.getAllocations(loc.id());
                suppliers.add(new Supplier(loc, allocations));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addSupplier(String addr, String phone, String contact, Map<Integer, Integer> productMap) {
        try {
            int locationId = locationService.addSupplierLocation(addr, phone, contact);
            Location newLocation = locationService.getLocation(locationId);

            for (Map.Entry<Integer, Integer> entry : productMap.entrySet())
                allocationDAO.addAllocation(locationId, entry.getKey(), entry.getValue());

            Supplier supplier = new Supplier(newLocation, productMap);
            suppliers.add(supplier);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getSupplierIds() {
        List<Integer> supplierIds = new ArrayList<>();
        for (Supplier supplier : suppliers) {
            supplierIds.add(supplier.getLocationId());
        }
        return supplierIds;
    }

    public List<Integer> getProductIds(int locationId) {
        for (Supplier supplier : suppliers) {
            if (supplier.getLocationId() == locationId) {
                return supplier.getProductIds();
            }
        }
        throw new IllegalArgumentException("Supplier not found at location: " + locationId);
    }

    public int getProductStock(int locationId, int productId) {
        for (Supplier supplier : suppliers) {
            if (supplier.getLocationId() == locationId) {
                return supplier.getProductStock(productId);
            }
        }
        throw new IllegalArgumentException("Supplier not found at location: " + locationId);
    }

    public void handleShipment(int supplierId, Map<Integer, Integer> itemsToLoad) {
        for (Supplier supplier : suppliers) {
            if (supplier.getLocationId() == supplierId) {
                supplier.handleShipment(itemsToLoad);

                try {
                    for (Map.Entry<Integer, Integer> entry : itemsToLoad.entrySet()) {
                        int updatedStock = supplier.getProductStock(entry.getKey());
                        allocationDAO.updateAllocation(supplierId, entry.getKey(), updatedStock);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
        throw new IllegalArgumentException("Supplier not found at location: " + supplierId);
    }

    public String getSupplierName(int locationId) {
        for (Supplier supplier : suppliers) {
            if (supplier.getLocationId() == locationId) {
                return supplier.getName();
            }
        }
        throw new IllegalArgumentException("Supplier not found at location: " + locationId);
    }

    public String getSupplierDisplay(int supplierId) {
        for (Supplier supplier : suppliers) {
            if (supplier.getLocationId() == supplierId) {
                return supplier.toString();
            }
        }
        throw new IllegalArgumentException("Supplier not found at location: " + supplierId);
    }

    public void resupplySupplier(int locationId, int productId, int amount) {
        for (Supplier supplier : suppliers) {
            if (supplier.getLocationId() == locationId) {
                supplier.addStock(productId, amount);
                try {
                    allocationDAO.updateAllocation(locationId, productId, supplier.getProductStock(productId));                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
        throw new IllegalArgumentException("Supplier not found at location: " + locationId);
    }
}