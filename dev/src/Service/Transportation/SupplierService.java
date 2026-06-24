package Service.Transportation;

import DAO.Transportation.SupplierAllocationDAO;
import DTO.Transportation.SupplierAllocationDTO;
import Domain.Transportation.Location;
import Domain.Transportation.Supplier;

import java.sql.SQLException;
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
        loadSuppliersFromDB();
    }

    public void loadSuppliersFromDB() {
        suppliers.clear();
        List<Location> supplierLocations = locationService.loadAllSuppliers();
        try {
            for (Location loc : supplierLocations) {
                // 1. קבלת רשימת DTOs מה-DAO
                List<SupplierAllocationDTO> dtoList = allocationDAO.getAllocations(loc.id());

                // 2. המרת הרשימה למפה (Map) עבור ה-Domain Object
                Map<Integer, Integer> allocations = new HashMap<>();
                for (SupplierAllocationDTO dto : dtoList) {
                    allocations.put(dto.productID(), dto.amountOfProduct());
                }

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

            for (Map.Entry<Integer, Integer> entry : productMap.entrySet()) {
                // המרה ל-DTO לפני שליחה ל-DAO
                allocationDAO.addAllocation(new SupplierAllocationDTO(locationId, entry.getKey(), entry.getValue()));
            }

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
                        // המרה ל-DTO לפני עדכון ה-DB.DB
                        allocationDAO.updateAllocation(new SupplierAllocationDTO(supplierId, entry.getKey(), updatedStock));
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
                    // המרה ל-DTO לפני עדכון ה-DB.DB
                    int updatedStock = supplier.getProductStock(productId);
                    allocationDAO.updateAllocation(new SupplierAllocationDTO(locationId, productId, updatedStock));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
        throw new IllegalArgumentException("Supplier not found at location: " + locationId);
    }
}