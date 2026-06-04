package Service;

import Domain.Location;
import Domain.Product;
import Domain.Supplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplierService {
    private final List<Supplier> suppliers;

    public SupplierService() {
        this.suppliers = new ArrayList<>();
    }

    public void addSupplier(Location location, Map<Product, Integer> productMap) {
        Supplier supplier = new Supplier(location, productMap);
        suppliers.add(supplier);
    }

    public List<String> getSuppliersDisplay() {
        List<String> display = new ArrayList<>();
        for (Supplier supplier : suppliers) {
            display.add(supplier.toString());
        }
        return display;
    }

    public Supplier getSupplierByIndex(int supplierIndex) {
        return suppliers.get(supplierIndex);
    }

    public Map<Supplier, Map<Integer, Integer>> mapIndicesToSuppliers(Map<Integer, Map<Integer, Integer>> supplierIndices) {
        Map<Supplier, Map<Integer, Integer>> supplierMap = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : supplierIndices.entrySet()) {
            supplierMap.put(getSupplierByIndex(entry.getKey()), entry.getValue());
        }
        return supplierMap;
    }

    public List<String> getProductNamesForSupplier(int supplierIdx) {
        List<String> productNames = new ArrayList<>();
        Supplier supplier = suppliers.get(supplierIdx);
        for (Product product : supplier.getProductsAvailable().keySet()) {
            productNames.add(product.name());
        }
        return productNames;
    }

    public int getProductStock(int supplierIdx, int i) {
        Supplier supplier = suppliers.get(supplierIdx);
        return supplier.getProductStock(i);
    }
}
