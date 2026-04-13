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

}
