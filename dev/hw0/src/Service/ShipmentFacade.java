package Service;

import Domain.*;
import Exceptions.DomainException;
import Exceptions.InsufficientSupplierStockException;
import Exceptions.InsufficientTruckStockException;
import Exceptions.OverweightException;

import java.time.LocalDate;
import java.util.*;

public class ShipmentFacade {

    private int shipmentNumber;
    private final List<Supplier> suppliers;

    public ShipmentFacade(List<Supplier> suppliers) {

        this.suppliers = suppliers;
    }

    public void addSupplier(Supplier supplier) {
        suppliers.add(supplier);
    }

    public void startShipment(Truck truck, Driver driver, Location source,
                              List<Destination> destinations, List<Truck> replacementTrucks) {

        Map<Supplier, List<ProductPair>> supplierAllocations = chooseSuppliersAndProducts();

        Transport transport = new Transport(LocalDate.now(), truck, driver, source, destinations,
                supplierAllocations, replacementTrucks);

        boolean shipmentFinish = false;
        while (!shipmentFinish)
            try {
                transport.processShipment();
                shipmentFinish = true;
            } catch (Exception e) {
                switch (e) {
                    case OverweightException oe -> {
                        System.out.println("⚠️ Overweight Problem: " + oe.getMessage());
                        handleOverweight();
                    }

                    case InsufficientSupplierStockException ise -> {
                        System.out.println("⚠️ Stock Problem: " + ise.getMessage());
                        handleInsufficientSupplierStock();
                    }

                    case InsufficientTruckStockException ise -> {
                        System.out.println("⚠️ Stock Problem: " + ise.getMessage());
                        handleInsufficientTruckStock();
                    }

                    case DomainException de -> {
                        System.out.println("General Domain Error: " + de.getMessage());
                        throw de;
                    }

                    default -> {
                        System.out.println("Critical System Error: " + e.getMessage());
                        throw e;
                    }
                }
            }
    }


    private Map<Supplier, List<ProductPair>> chooseSuppliersAndProducts() {
        Map<Supplier, List<ProductPair>> supplierAllocations = new LinkedHashMap<>();
        Scanner reader = new Scanner(System.in);

        showRequiredQuantities();
        displaySuppliers(suppliers);

        System.out.println("\nStep 1: Select Suppliers to visit");
        System.out.print("Enter indexes '1, 3, 4'.. or 'all': ");
        String input = reader.nextLine().trim();

        List<Supplier> selectedSuppliers = new ArrayList<>();

        if (input.equalsIgnoreCase("all")) {
            selectedSuppliers.addAll(suppliers);
        } else {
            String[] parts = input.split(",\\s*");
            for (String part : parts) {
                try {
                    int index = Integer.parseInt(part) - 1;
                    if (index >= 0 && index < suppliers.size()) {
                        Supplier s = suppliers.get(index);
                        if (!selectedSuppliers.contains(s)) selectedSuppliers.add(s);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        for (Supplier supplier : selectedSuppliers) {
            System.out.println(supplier.toString());

            List<ProductPair> productsToBuy = new ArrayList<>();

            while (true) {
                System.out.print("Enter product index (or 'done' to finish with this supplier): ");
                String prodInput = reader.nextLine().trim();

                if (prodInput.equalsIgnoreCase("done")) break;

                try {
                    int prodIndex = Integer.parseInt(prodInput) - 1;
                    Product product = supplier.getProductByIndex(prodIndex);

                    System.out.print("Enter quantity for " + product.name() + ": ");
                    int amount = Integer.parseInt(reader.nextLine().trim());

                    if (amount > 0) {
                        productsToBuy.add(new ProductPair(product, amount));
                        System.out.println("Added to list.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input, please try again.");
                }
            }

            if (!productsToBuy.isEmpty()) {
                supplierAllocations.put(supplier, productsToBuy);
            }
        }

        return supplierAllocations;
    }


    private void showRequiredQuantities() {

    }

    private void handleOverweight() {

    }

    private void handleInsufficientSupplierStock() {

    }

    private void handleInsufficientTruckStock() {

    }

    private void displaySuppliers(List<Supplier> supplierAllocations) {
        System.out.println("\n--- Available Suppliers ---");
        for (int i = 0; i < supplierAllocations.size(); i++) {
            Supplier supplier = supplierAllocations.get(i);
            System.out.println(("[" + (i + 1) + "] " + supplier.toString()));
        }
    }
}
