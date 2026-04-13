package Domain;

import java.util.ArrayList;
import java.util.List;

public class Location {

    private String address;
    private String phoneNumber;
    private String contactName;
    private List<ProductPair> productsAvailable;

    public Location(String address, String phoneNumber, String contactName) {
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.contactName = contactName;
        this.productsAvailable = new ArrayList<>();
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getContactName() {
        return contactName;
    }
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    public List<ProductPair> getProductsAvailable() {
        return productsAvailable;
    }
    public void setProductsAvailable(List<ProductPair> productsAvailable) {
        this.productsAvailable = productsAvailable;
    }

    public void addProductsAvailable(List<ProductPair> productsToDropOff) {
        if (productsToDropOff == null) return;

        for (ProductPair incoming : productsToDropOff) {
            String incomingName = incoming.getProduct().getName();
            boolean found = false;

            // 1. Check if we already have this product in our list
            for (ProductPair existing : this.getProductsAvailable()) {
                if (existing.getProduct().getName().equalsIgnoreCase(incomingName)) {
                    // Product exists! Update the amount
                    int newAmount = existing.getAmount() + incoming.getAmount();
                    existing.setAmount(newAmount);
                    found = true;
                    break;
                }
            }

            // 2. If it wasn't found, add it as a new entry
            if (!found) {
                // We create a new ProductPair object to avoid reference issues
                this.getProductsAvailable().add(new ProductPair(incoming.getProduct(), incoming.getAmount()));
            }
        }
    }

}
