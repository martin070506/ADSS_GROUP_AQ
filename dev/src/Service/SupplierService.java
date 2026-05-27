package Service;

import Domain.Location;
import Domain.Product;
import Domain.Supplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SupplierService {
    private final List<Supplier> suppliers;

    public SupplierService() {
        this.suppliers = new ArrayList<>();
    }

    public void registerSupplier(String addr, String ph, String contact, Map<Product, Integer> stock) {
        Location loc = new Location(addr, ph, contact);
        addSupplier(loc, stock);
    }

    public void addSupplier(Location location, Map<Product, Integer> productMap) {
        Supplier supplier = new Supplier(location, productMap);
        suppliers.add(supplier);
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public Supplier getSupplierByIndex(int supplierIndex) {
        return suppliers.get(supplierIndex);
    }
}
