package Service;

import Domain.Supplier;

import java.util.List;

public class ShipmentController {

    private int shipmentNumber;
    private List<Supplier> suppliers;

    public ShipmentController(List<Supplier> suppliers) {
        this.suppliers = suppliers;
    }


    public void addSupplier(Supplier supplier) {
        suppliers.add(supplier);
    }

}
