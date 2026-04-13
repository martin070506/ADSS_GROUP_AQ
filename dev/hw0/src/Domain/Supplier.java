package Domain;

import java.util.List;

public class Supplier{

    Location supplierLocation;
    public Supplier(Location location, List<ProductPair> products){
        this.supplierLocation = location;
        this.supplierLocation.setProductsAvailable(products);
    }

    public Location getSupplierLocation() {
        return supplierLocation;
    }

    public void printInventory() {
        System.out.println("--- Inventory for Supplier: " + supplierLocation.getContactName() + " ---");

        if (supplierLocation.getProductsAvailable() == null || supplierLocation.getProductsAvailable().isEmpty()) {
            System.out.println("No products currently in stock.");
            return;
        }

        for (ProductPair pair : supplierLocation.getProductsAvailable()) {
            System.out.println("- " + pair.toString());
        }
    }

    public List<ProductPair> getProductsAvailable(){
        return supplierLocation.getProductsAvailable();
    }

    public int getWeight(){
        int sum=0;
        for (ProductPair pair : supplierLocation.getProductsAvailable()) {
            sum+= pair.getAmount()*pair.getProduct().getWeight();
        }
        return sum;
    }


    public boolean handleShipment(Transport transport) {
        int heldWeight = 0;
        for(ProductPair pair : supplierLocation.getProductsAvailable()){
            heldWeight += pair.getProduct().getWeight()*pair.getAmount();
        }
        transport.addItems(supplierLocation.getProductsAvailable(),heldWeight);
        if(transport.getTruck().getAllowedWeight() >= transport.getCurrentThingsHeldWeight()+heldWeight){
            return true;
        }
        else {
            return false;
            /// here the Transport will handle knowing its gonna be overWeight
        }
    }

}
